importScripts('https://www.gstatic.com/firebasejs/5.9.2/firebase-app.js');
importScripts('https://www.gstatic.com/firebasejs/5.9.2/firebase-messaging.js');

// Initialize Firebase
let firebaseConfig = {
  apiKey: "AIzaSyAc4XrxZs2G1EVp-NbpCh5rw9rVgnUG284",
  authDomain: "scbj-af2e3.firebaseapp.com",
  projectId: "scbj-af2e3",
  storageBucket: "scbj-af2e3.appspot.com",
  messagingSenderId: "177564796245",
  appId: "1:177564796245:web:6b27b878cbc2ccacf39bdc",
  measurementId: "G-1YD7ZEM9HM"
};
firebase.initializeApp(firebaseConfig);
const messaging = firebase.messaging();