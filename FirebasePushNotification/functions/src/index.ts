'use-strict'
import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin'
admin.initializeApp(functions.config().firebase);

export const sendNotification = functions.firestore.document('Chats/User-Chat/{chatId}/{msgId}').onWrite((change, context)=>{
  
    const chatId = context.params.chatId
    const msgId = context.params.msgId

    console.log('Chat ID : '+ chatId + ' | MSG ID : ' + msgId)

})

// // // Start writing Firebase Functions
// // // https://firebase.google.com/docs/functions/typescript
// //
// // export const helloWorld = functions.https.onRequest((request, response) => {
// //  response.send("Hello from Firebase!");
// // });