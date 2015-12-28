/**
 * Created by Philipp on 21.12.2015.
 */
"use strict";

$(document).ready(function () {
    var graph = new Graph();
    var queryProgress = 0;
    var mime = 'text/x-java';
    // get mime type

    var editor = CodeMirror.fromTextArea(document.getElementById("code"), {
        mode: 'cypher',
        lineNumbers: true,
        theme: 'neo'
    });
    editor.setSize($(window).width()*0.6,50);

    new Vue({
        el: '#graphControl',
        data: {
            controls: [
                {text: "Alle Gruppen und Nodes", query: "MATCH a -[r:gehoert_zu]-> (b) RETURN r LIMIT 100;"}
            ]
        },
        methods: {
            select: function (query) {
                editor.setValue(query);
            }
        }
    });

   var runQueryUI =  new Vue({
        el: '#runQuery',
        data: {
            active: false,
            errorText: ""
        },
        methods: {
            run: function () {
                var query = editor.getValue();
                this.active = true;
                graph.clear();
                var self = this;
                this.errorText = "";
                dataService.query(query).then(function (data) {
                    runQueryUI.active = false;
                    if(data.errors.length!=0){
                        runQueryUI.errorText= data.errors[0].message;
                        return;
                    }
                    addNodesToGraph(data);
                    graph.skipSteps(500);
                    graph.run()
                }, function (err) {
                    runQueryUI.errorText= err.statusText;
                    runQueryUI.active = false;

                });
            }
        }
    });


    var graphInfoView = new Vue({
        el: '#graphInfoView',
        data: {
            nodeID: 'Hello Vue.js!',
            name: 'Hello Vue.js!',
            labels: 'Hello Vue.js!',
            links: 'Hello Vue.js!',
            key: 'Hello Vue.js!',
        },
        methods: {}
    });


    graph.mouseEnter(updateNode);
    graph.click(function () {
        graph.pinAllNodes()
    });
    var dataService = new DataService();


    function addNodesToGraph(data) {
        var items = data.results[0].data;
        graph.beginUpdate();
        items.forEach(function (item) {
            var nodes = item.graph.nodes;
            var relationships = item.graph.relationships;
            nodes.forEach(function (node) {
                var nodeData = node.properties;
                nodeData.labels = node.labels;
                nodeData.options = {
                    color: getColorByNode(node),
                    size: 12
                };
                graph.addNode(node.id, nodeData)
            });
            relationships.forEach(function (rel) {
                graph.addLink(rel.startNode, rel.endNode);
            });
        });
        graph.endUpdate();
    }

    function getColorByNode(node){
        switch(node.labels[0]) {
            case "artifact":
               return "ff8c00";
            case "group":
                return "00a6ff";
            default:
                return "e17df5";
        }

    }




    function updateNode(node) {
        graphInfoView.nodeID = node.id;
        graphInfoView.links = node.links;
        graphInfoView.key = node.data.id;
        graphInfoView.labels = node.data.labels;
        graphInfoView.name = node.data.name;
    }
});
