'use-strict'
import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin'
admin.initializeApp(functions.config().firebase);

exports.sendNotification = functions.firestore.document('Chats/User-Chat/{chatId}/{msgId}')
        .onWrite((change, context) => {
  
    const chatId = context.params.chatId;
    const msgId = context.params.msgId;

    // console.log('Chat ID : '+ chatId + '\n | MSG ID : ' + msgId );

    return admin.firestore().collection("Chats").doc("User-Chat").collection(chatId).doc(msgId).get().then(queryResult => {
        const senderId = queryResult.data().senderId;
        const recieverId = queryResult.data().receiverId;
        const msgBody = queryResult.data().msg;
        // console.log('From:  '+senderId + '  |  To:  ' + recieverId + '  | Message: ' + msgBody)

        const senderData = admin.firestore().collection("Users").doc(senderId).get();
        const receiverData = admin.firestore().collection("Users").doc(recieverId).get();

        return Promise.all([senderData, receiverData]).then(result => {
            const senderName = result[0].data().userName;
            const receiverName = result[1].data().userName;
            const registrationTokenId = result[1].data().registrationToken;

            console.log('Sender : ' + senderName + '| Receiver Name : ' + receiverName)

            const payload = {
                notification: {
                  title: 'Notification From '+senderName,
                  body: msgBody,
                  icon: "default",
                  click_action: "in.chatApp.firebasepushnotification.TARGETNOTIFICATION",
                  sound : "default"
                },
                data: {
                    message : msgBody,
                    fromId : senderId,
                }
              };

              return admin.messaging().sendToDevice(registrationTokenId,payload).then(_result => {
                  console.log("Notification Sent!");
              });
        });

    });


    // if(change!=null && context!=null  && change.after != null){
    //     const messageData = change.after.data();
    //     console.log(messageData);

    //     // return (messageData);       
    // }
})