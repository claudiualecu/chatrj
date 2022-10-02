
var sidepanelChat = new SidepanelChat();
function SidepanelChat(){
    this.templates = {};
    this.conversations = {};
    this.users = {};
    this.disabledToogle = false;
    this.conversationId = null;
    this.conversationName = null;
    this.messages = {};
    this.loadSidebarChat = function(){
        var that = this;
        var sideBar = document.querySelector('.side-bar');
        var arrowCollapse = document.querySelector('#logo-name__icon');
        var toogleBtn = document.querySelector(".side-bar-toggle");
        toogleBtn.onclick = () => {
            $("#sidepanel-chat").toggle("scale");
        };
        var newConversationButton = document.querySelector('#new-conversation-btn');
        newConversationButton.onclick = () => {
            $.ajax({
                context: this,
                type: 'get',
                url: '/user/getSysUsersExceptCurrent',
                async: false,
                success: function (data) {
                    that.users = data.users;
                    that.renderUsers();
                    $("#new-conversation-modal").modal({
                        escapeClose: true,
                        clickClose: true,
                        showClose: true
                    });
                },
                error: function (data) {
                    var rmsg = data.result + " - " + data.info;
                    console.log(rmsg);
                }
            });
        };
        var newMessageBtn = document.querySelector('#new-message-btn');
        newMessageBtn.onclick = () => {
            $.ajax({
                context: this,
                type: 'get',
                url: '/user/getSysUsersExceptCurrent',
                async: false,
                success: function (data) {
                    that.users = data.users;
                    that.renderUsersForMessage();
                    $("#new-message-modal").modal({
                        escapeClose: true,
                        clickClose: true,
                        showClose: true
                    });
                },
                error: function (data) {
                    var rmsg = data.result + " - " + data.info;
                    console.log(rmsg);
                }
            });
        };
        var saveConversation = document.querySelector("#save-conversation");
        saveConversation.onclick = () => {
            var conversation = that.prepareConversation();
            var reqData = JSON.stringify(conversation);
            $.ajax({
                context: this,
                type: 'post',
                data:reqData,
                contentType: "application/json",
                dataType:"json",
                url: '/conversation/addConversation',
                async: false,
                success: function (data) {
                    that.loadConversations();
                    $.modal.close();
                },
                error: function (data) {
                    var rmsg = data.result + " - " + data.info;
                    console.log(rmsg);
                }
            });
        };
        var saveNewMessage = document.querySelector("#save-new-message");
        saveNewMessage.onclick = () => {
            var userId = null;
            var elements = document.querySelectorAll(".selected-person-checkbox");
            Array.from(elements).forEach((element, index) => {

            if(element.checked){
                var id= element.id;
                userId = id.replaceAll("message-user-","");
            }
        });
            $.ajax({
                context: this,
                type: 'get',
                url: '/conversation/addNewMessage/'+userId,
                async: false,
                success: function (data) {
                    that.loadConversations();
                    if(data.id){
                        that.loadMessages(data.id,data.name);
                    }
                    $.modal.close();
                },
                error: function (data) {
                    var rmsg = data.result + " - " + data.info;
                    console.log(rmsg);
                }
            });
        };
        var sendMessage = document.querySelector("#chat-submit");
        var closeMessageBtn =document.querySelector(".chat-box-toggle");
        closeMessageBtn.onclick = () => {
            $("#chat-circle").toggle('scale');
            $(".chat-box").toggle('scale');
            that.disabledToogle=false;
            that.conversationId=null;
        }
    };
    this.parseTemplatesChat = function(){
        this.templates['tmpl_conversations'] = $('#tmpl_conversations').html();
        this.templates['tmpl_users'] = $('#tmpl_users').html();
        this.templates['tmpl_persons'] = $('#tmpl_persons').html();
        this.templates['tmpl_messages'] = $('#tmpl_messages').html();

        $.each(this.templates, function (index, template) {
            Mustache.parse(template);
        });
    };
    this.prepareConversation = function(){
        var name = $("#name").val();
        var userIdList = [];
        var elements = document.querySelectorAll(".selected-users-checkbox");
        Array.from(elements).forEach((element, index) => {
            if(element.checked){
            var id = element.id;
            var userId = id.replaceAll("user-","");
            userIdList.push(userId);
        }
    });
        var conversation = {
            name: name,
            userIdList: userIdList
        };
        return conversation;

    }
    this.loadConversations = function(){
        var that = this;

        try {

            $.ajax({
                context: this,
                type: 'get',
                url: '/conversation/getConversationList',
                async: false,
                success: function (data) {
                    that.conversations = data.conversations;
                    that.renderConversations();
                },
                error: function (data) {
                    var rmsg = data.result + " - " + data.info;
                    console.log(rmsg);
                }
            });

        } catch(e){
            console.log(e);
        }
    };
    this.loadNotification = function(){
        var that = this;

        try {

            $.ajax({
                context: this,
                type: 'get',
                url: '/conversation/getNotificationNumber',
                async: false,
                success: function (data) {
                    $(".count-chat").html(data.noUnreadMessages);
                },
                error: function (data) {
                    var rmsg = data.result + " - " + data.info;
                    console.log(rmsg);
                }
            });

        } catch(e){
            console.log(e);
        }
    };
    this.renderConversations = function(){
        var that = this;

        try {
            var render = Mustache.render(that.templates['tmpl_conversations'], {conversations: that.conversations});

            // curata html existent si pune noul html
            var div = $("#recent-chats");
            div.html('');
            div.append(render);
        } catch(e){
            console.log(e);
        }
    }
    this.renderUsers = function(){
        var that = this;

        try {
            var render = Mustache.render(that.templates['tmpl_users'], {users: that.users});

            var div = $("#selected-users");
            div.html('');
            div.append(render);
        } catch(e){
            console.log(e);
        }
    };
    this.renderUsersForMessage = function(){
        var that = this;

        try {
            var render = Mustache.render(that.templates['tmpl_persons'], {users: that.users});

            var div = $("#selected-person");
            div.html('');
            div.append(render);
        } catch(e){
            console.log(e);
        }
    };
    this.renderMessages = function(){
        var that = this;

        try {
            var render = Mustache.render(that.templates['tmpl_messages'], {messages: that.messages});

            var div = $(".chat-logs");
            div.html('');
            div.append(render);
            if (!that.disabledToogle) {
                $("#chat-circle").toggle('scale');
                $(".chat-box").toggle('scale');
            }
            var element = document.getElementsByClassName("chat-logs")[0];
            element.scrollTop = element.scrollHeight;
        } catch(e){
            console.log(e);
        }
    };
    this.loadMessages = function(conversationId,conversationName){
        var that = this;

        if(!that.disabledToogle){
            $("#conversation-title").html(conversationName);
            that.conversationId = conversationId;
            that.conversationName = conversationName;
            $.ajax({
                context: this,
                type: 'get',
                url: '/conversation/getMessagesList/'+that.conversationId,
                async: false,
                success: function (data) {
                    that.messages = data.messages;
                    that.renderMessages();
                    that.disabledToogle=true;
                },
                error: function (data) {
                    var rmsg = data.result + " - " + data.info;
                    console.log(rmsg);
                }
            });

        }
    };
    this.reloadMessages = function(){
        var that = this;

        $("#conversation-title").html(that.conversationName);
        $.ajax({
            context: this,
            type: 'get',
            url: '/conversation/getMessagesList/'+that.conversationId,
            async: false,
            success: function (data) {
                that.messages = data.messages;
                that.renderMessages();
            },
            error: function (data) {
                var rmsg = data.result + " - " + data.info;
                console.log(rmsg);
            }
        });


    };
}

$(document).ready(function () {
    debugger;
    sidepanelChat.parseTemplatesChat();
    sidepanelChat.loadConversations();
    sidepanelChat.loadSidebarChat();
});
