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
var tit_tmr;
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

function htmlFormat(pi) {
	var s = pi.song?pi.song:pi.title;
	var t = pi.track?pi.track+'#&nbsp;':'';
	var y = pi.year && pi.year != 0?pi.year:'';
	return t+'<strong>'+s+'</strong><br/><span style="color:#555555;font-weight:600">'+pi.artist+'</span><br/>'+pi.album+'<br/>'+y+'&nbsp;<i>'+pi.genre+'</i>';
}

var cur_tit;
function flashTit(mes) {	
	if (tit_tmr) {
		clearInterval(tit_tmr);
		tit_tmr = null;
	}	
	if (mes) {
		cur_tit = document.title;
		tit_tmr = setInterval(function() { if(document.title==cur_tit) document.title=mes; else document.title=cur_tit; }, 1200);
	} else if (cur_tit) {
		document.title=cur_tit;
		cur_tit = null;
	}	
}