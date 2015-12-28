/**
 * Created by Philipp on 21.12.2015.
 */
"use strict";

class Graph {

    constructor(elementId) {
        if(elementId==undefined)
            elementId = "graph";
        this.graph = Viva.Graph.graph();
        this.layout = Viva.Graph.Layout.forceDirected(this.graph, {
            springLength: 20,
            springCoeff: 0.0002,
            dragCoeff: 0.10,
            gravity: -1,
            timeStep: 5,

            /**
             * Maximum movement of the system which can be considered as stabilized
             */
            stableThreshold: 0.009
        });
        var nodeColor = 0x009ee8, // hex rrggbb
            nodeSize = 12;

        this.graphics = Viva.Graph.View.webglGraphics();
    /*    this.graphics.node(function (node) {
            var ui = Viva.Graph.View.webglSquare(2 + node.links.length, "#000");
            return ui;
        });*/

        // first, tell webgl graphics we want to use custom shader
        // to render nodes:
        var circleNode = new CircleNodeProgramm().build();
        this.graphics.setNodeProgram(circleNode);
        // second, change the node ui model, which can be understood
        // by the custom shader:
        this.graphics.node(function (node) {
            if(node.data.options!=undefined){

                var color = node.data.options.color;
                color  = parseInt(color,16);
                return new CircleNode(node.data.options.size, color);
            }
            return new CircleNode(nodeSize, nodeColor);
        });

        this.events = Viva.Graph.webglInputEvents(this.graphics, this.graph);
        this.renderer = Viva.Graph.View.renderer(this.graph,
            {
                layout: this.layout,
                graphics: this.graphics,
                container: document.getElementById(elementId),
                renderLinks: true
            });
    }

    run() {
        this.renderer.run();
    }

    skipSteps(n) {
        for (var i = 0; i < n; ++i) {
            this.layout.step();
        }
    }

    pinAllNodes() {
        var self = this;
        self.graph.forEachNode(function (node) {
            self.layout.pinNode(node, true);
        });
    }

    mouseEnter(cb) {
        return this.events.mouseEnter(cb)
    }

    mouseLeave(cb) {
        return this.events.mouseLeave(cb)
    }

    dblClick(cb) {
        return this.events.dblClick(cb)
    }

    click(cb) {
        return this.events.click(cb)
    }


    addNode(node,data) {
        this.graph.addNode(node,data)
    }

    addLink(node1, node2) {
        this.graph.addLink(node1, node2)
    }

    beginUpdate() {
        this.graph.beginUpdate();
    }

    endUpdate() {
        this.graph.endUpdate();
    }

    clear() {
        this.graph.clear();
    }
}