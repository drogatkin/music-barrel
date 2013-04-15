function playControl(c) {
		makeGenericAjaxCall('Player', 'cmd='+c, true, function(res) {

		}, function(err) {

		});
}