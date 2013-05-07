function playControl(c,oncompl) {
	makeGenericAjaxCall('Player', 'cmd=' + c, true, function(res) {
		if (oncompl) {
			if (res)
				oncompl(res);
		}
	}, function(err) {

	});
}

var mes_tmr;
function messg(s) {
	if (mes_tmr)
		clearTimeout(mes_tmr);
	var er = getElement('error');
	if (er) {
		er.innerHTML = s;
		if (s.length > 0)
			mes_tmr = setTimeout(function() {
				messg('')
			}, 20000);
		else
			mes_tmr = null;
	}
}