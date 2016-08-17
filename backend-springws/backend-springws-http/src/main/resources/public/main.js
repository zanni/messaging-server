/*
 * setup connection:
 * - create session in java backend
 * - open STOMP connection using session's data
 * - subscribe to queue
 */
function setupConnection(userId) {
	
	var date = function(){
		pad = function (str, len) {
		    return (new Array(len+1).join("0") + str).slice(-len);
		}
		var today = new Date();

		var h = pad(today.getHours(), 2);
		var m = pad(today.getMinutes(), 2);
		var s = pad(today.getSeconds(), 2);
		return h+":"+m+":"+s
		
	}
	var debug = function(msg) {
		msg = date() + " " +msg;
		$('#second div').append($("<code>").text(msg));
	}

	var log = function(msg) {
		msg = date() + " " +msg;
		$('#second div').append($("<code>").text(msg).css("color", "green"));
	}

	var error = function(msg) {
		msg = date() + " " +msg;
		$('#second div').append($("<code>").text(msg).css("color", "red"));
	}

	var received = function(msg) {
		msg = date() + " " +msg;
		$('#first div').append($("<code>").text(msg));
	}
	
	var clean = function(){
		$('#first div').empty();
		$('#second div').empty();
	}

	// create session
	$.post(
			'/connect',
			{
				userId : userId
			},
			function(data) {

				var user = data.userId;
				var pass = data.listeningKey;

				// construct connection obj
				if (data.listeningAddress.indexOf("ws://") !== -1) {
					var client = new WebSocket(data.listeningAddress + "?token="
							+ pass + "&userId=" + user);
				} else if (data.listeningAddress.indexOf("http://") !== -1) {
					var client = new SockJS(data.listeningAddress + "?token="
							+ pass + "&userId=" + user);
				}

				// bind 'connect' button
				$("#connect").off("click");
				$("#connect").on("click", function(e) {
					e.preventDefault();
					client.onclose = null;
					client.close();
					userId = prompt("Please enter your userId");
					if (userId) {
						setTimeout(function() {
							setupConnection(userId);
						}, 500);
					}

				});
				
				// bind 'reconnect' button
				$("#reconnect").off("click");
				$("#reconnect").on("click", function(e) {
					e.preventDefault();
					client.onclose = null;
					client.close();
					setTimeout(function() {
						setupConnection(userId);
					}, 500);
				});

				// bind 'disconnect' button
				$("#disconnect").off("click");
				$("#disconnect").on("click", function(e) {
					e.preventDefault();
					$('#connected').text("Not connected");
					client.onclose = null;
					client.close();
					debug("Disconnected");
				});

				$('#connected').text(
						"Connected as user: " + userId + " on server: "
								+ data.listeningAddress);

				client.onopen = function() {
					debug("Connected as user: " + userId + " on server: "
								+ data.listeningAddress);
				}
				client.onmessage = function(e) {
					received(e.data);
				};
				client.onclose = function() {
					log("Server deconnect, trying to reconnect...");
					 // make HTTP request to delete session
                    $.post('/disconnect', {
                        queueName: pass
                    }, function(data) {
                    	setupConnection(userId);
                    }).fail(function(err) {
                        error(e);
                    });

					
					
				};

				$("#submit").off("click");
				$("#submit").on("click", function(e) {
					e.preventDefault();
					var toUserId = $("#toUserId").val();
					var content = $("#content").val();
					$.post('/exchange', {
						fromUserId : userId,
						toUserId : toUserId,
						content : content
					}, function(data) {
						log("message successfully transmitted")
					}).fail(function(err) {
						var exp = JSON.parse(err.responseText);
						error(exp.message)

					});
				});

			}).fail(function(e) {
				var exp = JSON.parse(e.responseText);
				console.log('error', exp);
				error(exp.message)
				$('#connected').text("Not connected");
				// bind 'connect' button
				$("#connect").off("click");
				$("#connect").on("click", function(e) {
					e.preventDefault();
					userId = prompt("Please enter your userId");
					if (userId) {
						setupConnection(userId);
					}
				});
				// bind 'reconnect' button
				$("#reconnect").off("click");
				$("#reconnect").on("click", function(e) {
					e.preventDefault();
					setTimeout(function() {
						setupConnection(userId);
					}, 500);
				});

	});
}

var userId = prompt("Please enter your userId");
if (userId) {
	setupConnection(userId);
}

$("#clean").click(function() {
	$('#first div').empty();
	$('#second div').empty();
});
