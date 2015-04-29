# Pedalometer Android App
Visualizes the rotational speed of a bicycle pedal, by attaching an Android device to the ankle of a cyclist, which connects to a second device funcitoning as server and display. 

## Pedalometer Client
Can connect to Pedalometer Server using Bluetooth. Will then live-transmit xyz raw values from the accelerometer to the server.

## Pedalometer Server
Waits for connection from Client. After connection is established, the received raw data is stored in a model and the rotational speed of the client device is calculated and plotted.
