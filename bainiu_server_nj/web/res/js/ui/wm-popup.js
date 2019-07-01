define(["module","oEvent"],function(module,oEvent){
	function createNew(popupDom) {
		var popup = typeof popupDom == 'object' ? popupDom : document.getElementById(popupDom);
		for (var i = 1; i < arguments.length; i++) {
			var o = typeof arguments[i] == 'object' ? arguments[i] : document.getElementById(arguments[i]);
			oEvent.tap(o,(function(popup){
				if(popup.className == "c_popup") {
					popup.className = "c_popup c_popup-view";
				} else {
					popup.className = "c_popup";
				}
			}),popup)
		}
		return popup;
	}
	module.exports = function(){
		var popup = createNew.apply(null,arguments);
		
		this.show = function(){
			popup.className = "c_popup c_popup-view";
		}
		
		this.hide = function(){
			popup.className = "c_popup";
		}
	}
})