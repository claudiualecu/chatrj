package com.rj.chatrj.service;


import com.rj.chatrj.Util;
import com.rj.chatrj.model.Conversation;
import com.rj.chatrj.model.Message;
import com.rj.chatrj.model.StandardResponse;
import com.rj.chatrj.model.StandardResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;

@Service
public class ConversationService {


    private JdbcTemplate jdbcTemplate;
    private DataSource dataSource;

    @Autowired
    public ConversationService(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.dataSource = dataSource;
    }

    public List<Conversation> getConversations(Integer userId) throws Exception {

        return jdbcTemplate.query("select * from (\n" +
                "\n" +
                "select c.id,concat(pER.first_name,' ',pER.last_name) as name, count(case when COALESCE(um.readed,0)=0 AND UM.ID IS NOT NULL AND UM.USER_ID=? then 1 end)AS NO_UNREAD_MESSAGES,max(m.created_at)as message_date\n" +
                "                    from CONVERSATION C\n" +
                "left join MESSAGE M on M.CONVERSATION_ID=C.ID\n" +
                "LEFT JOIN USERS_MESSAGES UM ON UM.MESSAGE_ID=M.ID\n" +
                "left join PARTICIPANTS p on p.CONVERSATION_ID=c.id and p.USER_ID<>?\n" +
                "left join person per on per.USER_ID=p.USER_ID\n" +
                "where coalesce(c.private_conversation,0)=1 and C.ID IN (SELECT CONVERSATION_ID FROM PARTICIPANTS WHERE USER_ID=?)\n" +
                "                   GROUP BY C.ID,C.NAME,PER.FIRST_NAME,PER.LAST_NAME,per.user_id\n" +
                "union\n" +
                "select C.ID,C.NAME,COUNT(CASE WHEN COALESCE(UM.READED,0)=0 and um.user_id=? THEN 1 END) AS NO_UNREAD_MESSAGES,max(m.created_at)as message_date\n" +
                "from CONVERSATION C\n" +
                "left join MESSAGE M on M.CONVERSATION_ID=C.ID\n" +
                "LEFT JOIN USERS_MESSAGES UM ON UM.MESSAGE_ID=M.ID\n" +
                "WHERE ((UM.USER_ID=? or UM.ID IS NULL) or m.user_id=?) AND C.ID IN (SELECT CONVERSATION_ID FROM PARTICIPANTS WHERE USER_ID=?) and coalesce(c.private_conversation,0)=0\n" +
                "                   GROUP BY C.ID,C.NAME\n" +
                ") x order by COALESCE(message_date, cast('17530101' as datetime)) desc ", (rs, rowNum) -> {

            Conversation conversation = new Conversation();
            conversation.setId(rs.getInt("id"));
            conversation.setName(rs.getString("name"));
            conversation.setNoUnreadMessages(rs.getInt("no_unread_messages"));
            return conversation;
        }, userId, userId, userId, userId, userId, userId, userId);
    }

    public List<Message> getMessages(Integer userId, Integer conversationId) throws Exception {

        return jdbcTemplate.query("select m.message,m.user_id,count(case when um.readed=1 then 1 end) no_users_readed,count(um.id) no_users_total, m.created_at, format(m.created_at,'dd.MM.yyyy HH:mm') as created_at_str,concat(p.first_name,' ',p.last_name) as name\n" +
                "from message m\n" +
                "inner join users_messages um on um.message_id=m.id\n" +
                "inner join person p on p.user_id = m.user_id\n" +
                "where conversation_id = ?\n" +
                "group by m.message,m.user_id,m.CREATED_AT,p.first_name,p.last_name\n" +
                "order by m.created_at asc", (rs, rowNum) -> {

            Message message = new Message();
            message.setContent(rs.getString("message"));
            message.setUserId(rs.getInt("user_id"));
            message.setMines(rs.getInt("user_id") == userId);
            message.setReaded(rs.getInt("no_users_readed") == rs.getInt("no_users_total"));
            message.setCreatedAtStr(rs.getString("created_at_str"));
            message.setUserName(rs.getString("name"));
            return message;
        }, conversationId);
    }

    public Conversation addConversation(Integer userId, Conversation conversation) throws Exception {
        Conversation conversationResponse = new Conversation();
        try {
            Integer conversationId = Util.nextSeqVal(jdbcTemplate, "CONVERSATION_ID");
            jdbcTemplate.update("INSERT INTO CONVERSATION(ID, NAME) VALUES (?,?)", conversationId, conversation.getName());

            conversationResponse.setId(conversationId.intValue());
            for (Integer selectedId : conversation.getUserIdList()) {
                Integer participantsId = Util.nextSeqVal(jdbcTemplate, "PARTICIPANTS_ID");
                jdbcTemplate.update("INSERT INTO PARTICIPANTS(ID, CONVERSATION_ID, USER_ID) VALUES (?,?,?)", participantsId, conversationId, selectedId);
            }
            Integer participantsId = Util.nextSeqVal(jdbcTemplate, "PARTICIPANTS_ID");
            jdbcTemplate.update("INSERT INTO PARTICIPANTS(ID, CONVERSATION_ID, USER_ID) VALUES (?,?,?)", participantsId, conversationId, userId);

        } catch (Exception e) {
            conversationResponse.setId(null);
            conversationResponse.setResult(StandardResult.ERROR.toString());
            e.printStackTrace();
        }
        return conversationResponse;
    }

    public Conversation getNotificationNumber(Integer userId) throws Exception {

        Conversation conversation = new Conversation();

        Integer nr = jdbcTemplate.queryForObject("select count (0) nr from (select top 100 percent * from (\n" +
                "            select c.id,concat(pER.first_name,' ',pER.last_name) as name, count(case when COALESCE(um.readed,0)=0 AND UM.ID IS NOT NULL AND UM.USER_ID=? then 1 end)AS NO_UNREAD_MESSAGES,max(m.created_at)as message_date \n" +
                "                                from CONVERSATION C \n" +
                "            left join MESSAGE M on M.CONVERSATION_ID=C.ID\n" +
                "            LEFT JOIN USERS_MESSAGES UM ON UM.MESSAGE_ID=M.ID\n" +
                "            left join PARTICIPANTS p on p.CONVERSATION_ID=c.id and p.USER_ID<>?\n" +
                "            left join person per on per.USER_ID=p.USER_ID\n" +
                "            where coalesce(c.private_conversation,0)=1 and C.ID IN (SELECT CONVERSATION_ID FROM PARTICIPANTS WHERE USER_ID=?)\n" +
                "                               GROUP BY C.ID,C.NAME,PER.FIRST_NAME,PER.LAST_NAME,per.user_id\n" +
                "            union\n" +
                "            select C.ID,C.NAME,COUNT(CASE WHEN COALESCE(UM.READED,0)=0 and um.user_id=? THEN 1 END) AS NO_UNREAD_MESSAGES,max(m.created_at)as message_date\n" +
                "            from CONVERSATION C " +
                "            left join MESSAGE M on M.CONVERSATION_ID=C.ID \n" +
                "            LEFT JOIN USERS_MESSAGES UM ON UM.MESSAGE_ID=M.ID\n" +
                "            WHERE ((UM.USER_ID=? or UM.ID IS NULL) or m.user_id=?) AND C.ID IN (SELECT CONVERSATION_ID FROM PARTICIPANTS WHERE USER_ID=?) and coalesce(c.private_conversation,0)=0\n" +
                "                               GROUP BY C.ID,C.NAME\n" +
                "            ) x order by COALESCE(message_date, cast('17530101' as datetime)) desc)x where NO_UNREAD_MESSAGES>0 ", Integer.class, userId, userId, userId, userId, userId, userId, userId);
        conversation.setNoUnreadMessages(nr);
        return conversation;
    }

    public Message addMessageToConversation(Integer userId, Message message) throws Exception {
        Message response = new Message();
        Integer messageId = Util.nextSeqVal(jdbcTemplate, "MESSAGE_ID");
        jdbcTemplate.update("INSERT INTO MESSAGE(ID, CONVERSATION_ID, USER_ID, MESSAGE) VALUES (?,?,?,?)", messageId, message.getConversationId(), userId,  message.getContent());
        response.setUserId(userId);
        response.setContent(message.getContent());
        response.setConversationId(message.getConversationId());

        jdbcTemplate.query("select * from participants where conversation_id=? and user_id<>?", (rs,i)->{
            Integer ussersMessagesId = Util.nextSeqVal(jdbcTemplate, "USSERS_MESSAGES_ID");
            jdbcTemplate.update("INSERT INTO USSERS_MESSAGES(ID, MESSAGE_ID, USER_ID) VALUES (?,?,?)", ussersMessagesId, messageId, userId);
            return 0;
        },message.getConversationId(), userId);

        return response;
    }

    public Conversation addNewMessage(Integer userId, Integer userIdTo) throws Exception {
        Conversation response = new Conversation();
        Integer checkExists = jdbcTemplate.queryForObject("select count(0) check_exists from (SELECT c.id,count(1) nr_total,count(case when p.USER_ID=? or p.user_id=? then 1 end)nr\n" +
                "FROM conversation c left join PARTICIPANTS p on p.CONVERSATION_ID=c.id where c.private_conversation=1\n" +
                "group by c.id)x where nr_total=nr", Integer.class, userId, userIdTo);
            if (checkExists!=null && checkExists.equals(0)) {
                Integer conversationId = Util.nextSeqVal(jdbcTemplate, "CONVERSATION_ID");
                jdbcTemplate.update("INSERT INTO CONVERSATION(ID, PRIVATE_CONVERSATION) VALUES (?,?)",conversationId, 1);

                Integer participantsId = Util.nextSeqVal(jdbcTemplate, "PARTICIPANTS_ID");
                jdbcTemplate.update("INSERT INTO PARTICIPANTS(ID, CONVERSATION_ID, USER_ID) VALUES (?,?,?)", participantsId, conversationId, userId);

                participantsId = Util.nextSeqVal(jdbcTemplate, "PARTICIPANTS_ID");
                jdbcTemplate.update("INSERT INTO PARTICIPANTS(ID, CONVERSATION_ID, USER_ID) VALUES (?,?,?)", participantsId, conversationId, userIdTo);

            } else {
                jdbcTemplate.query("select c.id,concat(pER.first_name,' ',pER.last_name) as name,per.user_id as user_id_to, count(case when um.readed=1 then 1 end)AS NO_UNREAD_MESSAGES\n" +
                        "                    from CONVERSATION C\n" +
                        " left join MESSAGE M on M.CONVERSATION_ID=C.ID\n" +
                        " LEFT JOIN USERS_MESSAGES UM ON UM.MESSAGE_ID=M.ID\n" +
                        " left join PARTICIPANTS p on p.CONVERSATION_ID=c.id and p.USER_ID<>?\n" +
                        " left join person per on per.USER_ID=p.USER_ID\n" +
                        " where coalesce(c.private_conversation,0)=1\n" +
                        "                    GROUP BY C.ID,C.NAME,PER.FIRST_NAME,PER.LAST_NAME,per.user_id " +
                        " having per.user_id=?", (rs, i)->{
                    response.setId(rs.getInt("id"));
                    response.setName(rs.getString("name"));
                    return 0;
                }, userId, userIdTo);
            }

        return response;
    }

    public StandardResponse readMessages(Integer userId, Integer conversationId, String token) throws Exception {
        StandardResponse standardResponse = new StandardResponse();
        standardResponse.setResult(StandardResult.OK.toString());
        try {
            jdbcTemplate.update("update users_messages set readed=1 where user_id=? and message_id in (select id from message where conversation_id=?)",userId, conversationId);
        } catch (Exception e) {
            standardResponse.setResult(StandardResult.ERROR.toString());
        }
        return standardResponse;
    }
}
