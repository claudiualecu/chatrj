var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#messages").html("");
}

function connect() {
    var socket = new SockJS('/websocket-messaging');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function () {
        setConnected(true);
        stompClient.subscribe('/topic/message', function (result) {
            showMessage(JSON.parse(result.body).content);
        });
        stompClient.subscribe('/topic/typing', function (result) {
            showTyping(JSON.parse(result.body).content);
        });
        stompClient.subscribe('/topic/readMessages', function (result) {
            sidepanelChat.loadConversations();
            sidepanelChat.loadNotification();

        });

    });

}



function sendMessage() {
    stompClient.send("/app/message", {}, JSON.stringify({'content': $("#chat-input").val(), conversationId: sidepanelChat.conversationId}));
    $(".chat-typing").html("");
    $("#chat-input").val("");
}

function sendTyping() {
    stompClient.send("/app/typing", {});
}

function showMessage(message) {
    sidepanelChat.loadConversations();
    sidepanelChat.reloadMessages();
    sidepanelChat.loadNotification();
    $(".chat-typing").html("");

}

function showConnectedUsers(users) {
    var userTable = $("#members-connected");
    userTable.html("");
    if(users){
        for(var i = 0; i < users.length; i++){
            var imgNumber = (i+1)%5;
            userTable.append("<img src=\"../images/avatar"+imgNumber+".png\" title=\""+users[i].name+"\" class=\"avatar\">")
        }
    }
}

function showTyping(message) {
    $(".chat-typing").html(message);
    setTimeout(function () {
        $(".chat-typing").html("");
    }, 3000);
}

function readMessages(conversationId){
    if(conversationId){
        stompClient.send("/app/readMessages", {}, JSON.stringify({id:conversationId}));
    }
}
$(function () {
    connect();
    $("#chat-submit").click(function () {
        sendMessage();
    });
    $("#chat-input").keypress(function (e) {
        var key = e.which;
        if(key == 13)  // the enter key code
        {
            $('button[id = chat-submit]').click();
        }
        sendTyping();
    });
    $("#chat-input").focusin(function (e) {
        readMessages(sidepanelChat.conversationId);
    });


});