/**
 * appframework.ui - A User Interface library for App Framework applications
 */
 /* global wm*/
 /* global numOnly*/
 /* jshint camelcase:false*/
define(["jcl","wmCss3animate"],function($){
	"use strict";
	var hasLaunched = false;
    var startPath = window.location.pathname + window.location.search;
    var defaultHash = window.location.hash;
    var previousTarget = defaultHash;
    var ui = function() {
        // Init the page
        var that = this;

        /**
         * Helper function to setup the transition objects
         * Custom transitions can be added via $.ui.availableTransitions
           ```
           $.ui.availableTransitions['none']=function();
           ```
           */

        this.availableTransitions = {};
        this.availableTransitions["default"] = this.availableTransitions.none = this.noTransition;
        //setup the menu and boot touchLayer

		//Hack  for AMD/requireJS support
        //set autolaunch = false
        if ((typeof define === "function" && define.amd)||(typeof module !== "undefined" && module.exports)) {
            that.autoLaunch=false;
        }

		if (document.readyState === "complete" || document.readyState === "loaded") {
            that.autoBoot();
           
        } else $(document).ready(
            function(){
                that.autoBoot();
            },
        false);

    };


    ui.prototype = {
        transitionTime: "230ms",
        remotePages: {},
        hasLaunched: false,
        isLaunching:false,
        launchCompleted: false,
        useAutoPressed: true,
		autoBoot: function() {
            this.hasLaunched = true;
            var that=this;
            if (this.autoLaunch) {
				this.launch();
            }
        },
        css3animate: function(el, opts) {
            el = $(el);
            return el.css3Animate(opts);
        },
        dispatchPanelEvent:function(fnc, myPanel){
            if (typeof fnc === "string" && window[fnc]) {
                return window[fnc](myPanel);
            }
            else if(fnc.indexOf(".")!==-1){
                var scope=window,items=fnc.split("."),len=items.length,i=0;
                for(i;i<len-1;i++){
                    scope=scope[items[i]];
                    if(scope===undefined) return;
                }
                return scope[items[i]](myPanel);
            }
        },
        /**
         * this will disable native scrolling on iOS
            ```
            $.ui.disableNativeScrolling);
            ```
         *@title $.ui.disableNativeScrolling
         */
        disableNativeScrolling: function() {
            $.feat.nativeTouchScroll = false;
        },
        /**
         * Boolean if you want to auto launch afui
           ```
           $.ui.autoLaunch = false; //
         * @title $.ui.autoLaunch
         */
        autoLaunch: true,
        /**
         * function to fire when afui is ready and completed launch
           ```
           $.ui.ready(function(){console.log('afui is ready');});
           ```
         * @param {function} param function to execute
         * @title $.ui.ready
         */
        ready: function(param) {
            if (this.launchCompleted)
                param();
            else {
				var wmui = $("#wmui");
				if(wmui.length){
					wmui.ready(function(){
						param();
					});
				}else{
					$(document).ready(function(e) {
						param();
					});
				}
            }
        },
        /**
         * Helper function that parses a contents html for any script tags and either adds them or executes the code
         * @api private
         */
        parseScriptTags: function(div) {
            if (!div) return;
            $.parseJS(div);
        },
        /**
         * This executes the transition for the panel
            ```
            $.ui.runTransition(transition,oldDiv,currDiv,back)
            ```
         * @api private
         * @title $.ui.runTransition(transition,oldDiv,currDiv,back)
         */
        runTransition: function(transition, oldDiv, currWhat, back) {
            if (!this.availableTransitions[transition]) transition = "default";
            if(oldDiv.style.display==="none")
                oldDiv.style.display = "block";
            if(currWhat.style.display==="none")
                currWhat.style.display = "block";
            this.availableTransitions[transition].call(this, oldDiv, currWhat, back);
        },
		/**
         * This is callled when you want to launch afui.  If autoLaunch is set to true, it gets called on DOMContentLoaded.
         * If autoLaunch is set to false, you can manually invoke it.
           ```
           $.ui.autoLaunch=false;
           $.ui.launch();
           ```
         * @title $.ui.launch();
         */
        launch: function() {
			if (this.hasLaunched === false || this.launchCompleted) {
                this.hasLaunched = true;
                return;
            }
            if(this.isLaunching)
                return true;
            this.isLaunching=true;

            var that = this;

			that.launchCompleted = true;
		},
        /**
         * This is the default transition.  It simply shows the new panel and hides the old
         */
        noTransition: function(oldDiv, currDiv, back) {
            currDiv.style.display = "block";
            oldDiv.style.display = "block";
            var that = this;
            that.clearAnimations(currDiv);
            that.css3animate(oldDiv, {
                x: "0%",
                y: 0
            });
            that.finishTransition(oldDiv);
            currDiv.style.zIndex = 2;
            oldDiv.style.zIndex = 1;
        },
        /**
         * This must be called at the end of every transition to hide the old div and reset the doingTransition variable
         *
         * @param {object} oldDiv Div that transitioned out
         * @param {object=} currDiv 
         * @title $.ui.finishTransition(oldDiv)
         */
        finishTransition: function(oldDiv, currDiv) {
            oldDiv.style.display = "none";
            this.doingTransition = false;
            if (currDiv) this.clearAnimations(currDiv);
            if (oldDiv) this.clearAnimations(oldDiv);
            $.trigger(this, "content-loaded");
        },

        /**
         * This must be called at the end of every transition to remove all transforms and transitions attached to the inView object (performance + native scroll)
         *
         * @param {object} inViewDiv Div that transitioned out
         * @title $.ui.finishTransition(oldDiv)
         */
        clearAnimations: function(inViewDiv) {
            inViewDiv.style[$.feat.cssPrefix + "Transform"] = "none";
            inViewDiv.style[$.feat.cssPrefix + "Transition"] = "none";
        }

        /**
         * END
         * @api private
         */
    };

    $.ui = new ui();
    $.ui.init=true;

    return $;
});