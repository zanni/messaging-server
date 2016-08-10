
/*
 * setup connection:
 * - create session in java backend
 * - open STOMP connection using session's data
 * - subscribe to queue
 */
function setupConnection(userId) {

    var log = function(msg) {
        $('#second div').append($("<code>").text(msg).css("color", "green"));
    }

    var error = function(msg) {
        $('#second div').append($("<code>").text(msg).css("color", "red"));
    }

    // create session
    $
        .post(
            '/connect', {
                userId: userId
            },
            function(data) {

                var user = data.userId;
                var pass = data.listeningKey;

                // construct connection obj
                if (data.listeningAddress.indexOf("ws://") !== -1) {
                    var ws = new WebSocket(data.listeningAddress);
                } else if (data.listeningAddress.indexOf("http://") !== -1) {
                    var ws = new SockJS(data.listeningAddress);
                } 
                var client = Stomp.over(ws);
                
                // bind 'connect' button
                $("#connect").off("click");
                $("#connect").on("click", function(e) {
                    e.preventDefault();
                    if(client){
                    	client.disconnect();
                    }
                    $('#first div').empty();
                    $('#second div').empty();
                    userId = prompt("Please enter your userId");
                    if (userId) {
                        setupConnection(userId);
                    }

                });
                
             // bind 'disconnect' button
                $("#disconnect").off("click");
                $("#disconnect").on("click", function(e) {
                    e.preventDefault();
                    $('#connected').text("Not connected");
                    // make HTTP request to delete session
                    $.post('/disconnect', {
                        queueName: pass
                    }, function(data) {
                        client.disconnect();
                        log("successfully disconnected");
                    }).fail(function(err) {
                        error("FAIL to disconnect");
                    });
                });

                $('#connected').text("Connected as user: " + userId);

                client.debug = function(e) {
                    $('#second div').append($("<code>").text(e));
                };

                //
                var on_connect_closure = function(session) {
                    return function(x) {
                        log("successfully connected to rabbitmq, attempting to subscribe to queue");
                        id = client.subscribe("/queue/" +
                        		session.listeningKey,
                            function(m) {
                                $('#first div').append(
                                    $("<code>").text(m.body));
                            }, {
                                'auto-delete': true,
                                'durable': false,
                                'exclusive': true
                            });
                    };
                    s
                }
                var on_error = function(e) {
                    console.log('error', e);
                    $('#second div').append($("<code>").text(e));
                };
                client.connect(user, pass, on_connect_closure(data),
                    on_error, '/');

                $("#submit").on("click", function(e) {
                    e.preventDefault();
                    var toUserId = $("#toUserId").val();
                    var content = $("#content").val();
                    $.post('/exchange', {
                        fromUserId: userId,
                        toUserId: toUserId,
                        content: content
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
                $('#first div').empty();
                $('#second div').empty();
                userId = prompt("Please enter your userId");
                if (userId) {
                    setupConnection(userId);
                }

            });
            
        });
}

var userId = prompt("Please enter your userId");
if (userId) {
    setupConnection(userId);
}

$("#cleanRetreive").click(function(){
	$('#first div').empty();
});
$("#cleanLog").click(function(){
	$('#second div').empty();
});