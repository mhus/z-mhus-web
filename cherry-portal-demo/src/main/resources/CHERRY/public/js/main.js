
function setAjaxDebug(x) {
    if (!localStorage) return;
    if (x == true)
    localStorage.setItem('debugAjax', x);
    else
    localStorage.removeItem('debugAjax');
}

function isAjaxDebug() {
    return (localStorage && localStorage.getItem('debugAjax') );
    // || window.location.search.indexOf('_debug_ajax=true') > 0;
}


function sendGetRequest (_path,_params) {

    var restResult = null;

    if(isAjaxDebug()){
        console.log(">>> sendRestRequest: " + _path  + " " + JSON.stringify(_params));
    }

    if (!_path)
    	_path = "";
    if (!_params)
    	_params = "";

    if(sessionStorage.getItem('trace')==='MAP'){
        _params['_trace']= 'MAP';
    }

    var paramsStr = "";
    for (var key in _params) {
        if(key != null && key != undefined && key != '')
        paramsStr+= encodeURIComponent(key) + '=' + encodeURIComponent(_params[key]) + '&';

        console.log('key ===> ' + key + '|| paramsStr ===> ' + paramsStr);
    }

    $.ajax({
        type: "GET",
        url: _path + "?"  + paramsStr,
        contentType: "application/json",
        dataType: "json",
        async: false,
        cache: false,
        timeout: 60000,
        success: function(data) {
            if(isAjaxDebug()){
                console.log("<<< sendRestRequest: " + JSON.stringify(data));
                console.log("<<< sendRestRequest: success");
            }
            restResult = data;
        },
        error: function(e) {
            if(isAjaxDebug()){
                console.log("+++ sendRestRequest: " + JSON.stringify(e));
                console.log("<<< sendRestRequest: error");
            }
        },
        complete: function(data) {
            if(isAjaxDebug()){
                console.log("+++ sendRestRequest: " + JSON.stringify(data));
                console.log("<<< sendRestRequest: comlpete");
            }
        }
    });

    return restResult;
}

function sendPostRequest(_path,_params) {

    var restResult = null;

    if(isAjaxDebug()){
        console.log(">>> sendRestPostRequest: " + _path + " " + JSON.stringify(_params));
    }

    if (!_path)
    	_path = "";
    if (!_params)
    	_params = "";
    if(sessionStorage.getItem('trace')==='MAP'){
        _params['_trace']= 'MAP';
    }
    $.ajax({
        type: "POST",
        url: _path + _id,
        data: _params,
        async: false,
        cache: false,
        timeout: 60000,
        success: function(data) {
            if(isAjaxDebug()){
                console.log("<<< sendRestPostRequest: " + JSON.stringify(data));
                console.log("<<< sendRestPostRequest: success");
            }
            restResult = data;
        },
        error: function(e) {
            if(isAjaxDebug()){
                console.log("+++ sendRestPostRequest: " + JSON.stringify(e));
                console.log("<<< sendRestPostRequest: error");
            }
        },
        complete: function(data) {
            if(isAjaxDebug()){
                console.log("<<< sendRestPostRequest: " + JSON.stringify(data));
                console.log("<<< sendRestPostRequest: comleted");
            }
        }
    });

    return restResult;
}
