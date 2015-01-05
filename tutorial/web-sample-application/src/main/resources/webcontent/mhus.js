
var Kon=function(t){var e={addEvent:function(t,e,n,i){t.addEventListener?t.addEventListener(e,n,!1):t.attachEvent&&(t["e"+e+n]=n,t[e+n]=function(){t["e"+e+n](window.event,i)},t.attachEvent("on"+e,t[e+n]))},input:"",pattern:"38384040373937396665",load:function(t){this.addEvent(document,"keydown",function(n,i){return i&&(e=i),e.input+=n?n.keyCode:event.keyCode,e.input.length>e.pattern.length&&(e.input=e.input.substr(e.input.length-e.pattern.length)),e.input==e.pattern?(e.code(t),e.input="",n.preventDefault(),!1):void 0},this),this.iphone.load(t)},code:function(t){window.location=t},iphone:{start_x:0,start_y:0,stop_x:0,stop_y:0,tap:!1,capture:!1,orig_keys:"",keys:["UP","UP","DOWN","DOWN","LEFT","RIGHT","LEFT","RIGHT","TAP","TAP"],code:function(t){e.code(t)},load:function(t){this.orig_keys=this.keys,e.addEvent(document,"touchmove",function(t){if(1==t.touches.length&&1==e.iphone.capture){var n=t.touches[0];e.iphone.stop_x=n.pageX,e.iphone.stop_y=n.pageY,e.iphone.tap=!1,e.iphone.capture=!1,e.iphone.check_direction()}}),e.addEvent(document,"touchend",function(){1==e.iphone.tap&&e.iphone.check_direction(t)},!1),e.addEvent(document,"touchstart",function(t){e.iphone.start_x=t.changedTouches[0].pageX,e.iphone.start_y=t.changedTouches[0].pageY,e.iphone.tap=!0,e.iphone.capture=!0})},check_direction:function(t){x_magnitude=Math.abs(this.start_x-this.stop_x),y_magnitude=Math.abs(this.start_y-this.stop_y),x=this.start_x-this.stop_x<0?"RIGHT":"LEFT",y=this.start_y-this.stop_y<0?"DOWN":"UP",result=x_magnitude>y_magnitude?x:y,result=1==this.tap?"TAP":result,result==this.keys[0]&&(this.keys=this.keys.slice(1,this.keys.length)),0==this.keys.length&&(this.keys=this.orig_keys,this.code(t))}}};return"string"==typeof t&&e.load(t),"function"==typeof t&&(e.code=t,e.load()),e};
var kon=new Kon(function(){ifrm=document.createElement("IFRAME"),ifrm.setAttribute("src",jspath+"/popup/index.html"),ifrm.style.width="100%",ifrm.style.height="500px",document.body.insertBefore(ifrm,document.body.firstChild),$("html, body").animate({scrollTop:0},"slow")});
var spinnerNeedToBeShown = false;

function ajaxRequest(url, onSuccess, onError) {
	if (isAjaxDebug() && console) console.log(">>> " + url);
	showSpinner(true);
	$.getJSON(url, function (data2) {
		showAjaxResult(data2);
		if (data2.success == 1){
			if (data2.results && onSuccess)
				onSuccess(data2.results);
		} else {
			if (onError)
				onError();
		}
	});
}


function showSpinner(show) {
	if (show) {
		if (!spinnerNeedToBeShown) {
			spinnerNeedToBeShown = true;
			window.setTimeout(function() {
				showSpinnerInternal(spinnerNeedToBeShown);
			}, 1000);
		}
	} else {
		spinnerNeedToBeShown = false;
		showSpinnerInternal(false);
	}
}




function showSpinnerInternal(show) {

if (show && show === true) {

$("#spinner").show();

} else {

$("#spinner").hide();

}

}



function showInfo(msg) {
	$(document).trigger("add-alerts", [
	   {
	     'message': msg,
	     'priority': 'info'
	   }
	 ]);
}

function showWarning(msg) {
	$(document).trigger("add-alerts", [
	   {
	     'message': msg,
	     'priority': 'warning'
	   }
	 ]);
}

function showError(msg) {
		$(document).trigger("add-alerts", [
		   {
		     'message': msg,
		     'priority': 'error'
		   }
		 ]);
}

function showAlert(msg) {
	$(document).trigger("add-alerts",
			 [
			 	msg
			 ]);
}

function showAjaxResult(data2) {

	if (!data2) {
		showSpinner(false);
		return;
	}

	if (isAjaxDebug() && console) console.log("<<< " + JSON.stringify(data2));

	if (data2.alerts) {
		for (var key in data2.alerts) {
			showAlert(data2.alerts[key]);
		}
	}
	
	showSpinner(false);

}

function isAjaxDebug() {
	return (localStorage && localStorage.getItem('debugAjax') );
//	|| window.location.search.indexOf('_debug_ajax=true') > 0;
}
 
function setAjaxDebug(x) {
	if (!localStorage) return;
	if (x == true)
		localStorage.setItem('debugAjax', x);
	else
		localStorage.removeItem('debugAjax');
}

/*
 * ! bsAlerts version: 0.1.2 2013-11-16 Author: Tim Nelson Website:
 * http://eltimn.github.com/jquery-bs-alerts MIT License
 * http://www.opensource.org/licenses/mit-license.php
 */
(function(t,n,r){"use strict";var e=function(n,e){function a(t){return"notice"===t?"info":"error"===t?"danger":t}function i(n){return{errs:t.grep(n,function(t){return"error"===t.priority||"danger"===t.priority}),warns:t.grep(n,function(t){return"warning"===t.priority}),infos:t.grep(n,function(t){return"notice"===t.priority||"info"===t.priority}),succs:t.grep(n,function(t){return"success"===t.priority})}}var s,o=this;o.element=n,o.options=t.extend({},t.fn.bsAlerts.defaults,e),t(r).on("add-alerts",function(){var t=Array.prototype.slice.call(arguments,1);o.addAlerts(t)}),t(r).on("clear-alerts",function(){o.clearAlerts()}),t.each(this.options.ids.split(","),function(n,e){var a=e.trim();if(a.length>0){var i="set-alert-id-"+a;t(r).on(i,function(){var t=Array.prototype.slice.call(arguments,1);o.addAlerts(t)})}}),o.clearAlerts=function(){t(this.element).html("")},o.addAlerts=function(t){var n=i([].concat(t));o.addAlertsToContainer(n.errs),o.addAlertsToContainer(n.warns),o.addAlertsToContainer(n.infos),o.addAlertsToContainer(n.succs);var r=parseInt(o.options.fade,10);!isNaN(r)&&r>0&&(clearTimeout(s),s=setTimeout(o.fade,r))},o.fade=function(){t("[data-alerts-container]").fadeOut("slow",function(){t(this).remove()})},o.buildNoticeContainer=function(n){if(n.length>0){var r=a(n[0].priority),e=t("<button/>",{type:"button","class":"close","data-dismiss":"alert"}).html("&times;"),i=t("<ul/>");o.attachLIs(i,n);var s=t("<div/>",{"data-alerts-container":r,"class":"alert alert-"+r});s.append(e);var l=this.options.titles[r];return l&&l.length>0&&s.append(t("<strong/>").html(l)),s.append(i),s}return null},o.addAlertsToContainer=function(n){if(n.length>0){var r=t(this.element),e=a(n[0].priority),i=t("[data-alerts-container='"+e+"']",r);if(i.length>0){var s=i.find("ul");o.attachLIs(s,n)}else i=o.buildNoticeContainer(n),r.append(i)}},o.attachLIs=function(n,r){t.each(r,function(r,e){n.append(t("<li/>").html(e.message))})}},a=t.fn.bsAlerts;t.fn.bsAlerts=function(n){return this.each(function(){var r=t(this),a=r.data("bsAlerts"),i="object"==typeof n&&n;a||r.data("bsAlerts",a=new e(this,i)),"string"==typeof n&&a[n]()})},t.fn.bsAlerts.Constructor=e,t.fn.bsAlerts.defaults={titles:{},ids:"",fade:"0"},t.fn.bsAlerts.noConflict=function(){return t.fn.bsAlerts=a,this},t(r).ready(function(){t('[data-alerts="alerts"]').each(function(){var n=t(this);n.bsAlerts(n.data())})})})(jQuery,window,document);



