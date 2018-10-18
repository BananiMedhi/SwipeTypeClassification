# Magnet Swipe Classification
This is an Android app which detects type of a magnet swipe on the back of the Android phone

This is a sequence classification problem.
Most android phones today have an inbuilt magentic field sensor. The sensor values were captured for repeated horizontal and vertical swipes on the back of an android phone in two different files, using a small magnet. This formed the training data. The data was trained and an LSTM model was built.
Finally, an Android app was created which sends magnet swipe data to the python flask web app. It classfies the sequence type and sends response back to the phone which displays it on its screen.

