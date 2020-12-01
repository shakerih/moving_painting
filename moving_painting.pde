
import processing.video.*;
import processing.net.*;

import processing.serial.*;

Serial myPort;

Server s; 
Client c;

Capture cam;
Command cmd;
PImage photo;
int time;
int distance;
JPGEncoder jpg;

static final String APP = "python ";
static final String FILE = "C:/Users/omlette/Documents/moving_painting/moving_painting/data/painting.py ";

void setup() {
  fullScreen();
  println("here");
  String[] cameras = Capture.list();
  println(cameras.length);
  if (cameras.length == 0) {
    println("There are no cameras available for capture.");
    exit();
  } else {
    println("Available cameras:");
    for (int i = 0; i < cameras.length; i++) {
      println(cameras[i]);
    }

    // The camera can be initialized directly using an 
    // element from the array returned by list():
    cam = new Capture(this, cameras[0]);
    cam.start();
  }
  photo=loadImage("data/resources/base.png");
  time = second();
  photo.resize(width, 0);

  s = new Server(this, 12345);

  jpg = new JPGEncoder();

  String portName = Serial.list()[0]; 
  myPort = new Serial(this, portName, 9600);
}


void draw() {

  if (cam.available() == true) {
    cam.read();
  }


  int currtime = second() - time;
  image(photo, 0, 0);
  
  if(myPort.available() > 0){
   distance = myPort.read(); 
  }

  if (currtime % 2 == 0) {
    println(distance);
    cam.save("data/resources/base.png");
    cmd = new Command(APP+FILE+int(map(distance, 0, 200, 20, 5)));
    println(cmd.command, ENTER);

    cmd.run();
    photo=loadImage("data/example_images/current.png");

    photo.resize(width, 0);


    try {
      // we try to encode it
      byte[] jpgBytes = jpg.encode( photo, 0.99F );
      // Taken from: https://processing.org/discourse/beta/num_1192330628.html
      // if all goes well, we prepare the bytes that represent the length
      int l = jpgBytes.length;
      byte[] lengthBytes = new byte[]{ (byte)( l & 0xFF ), (byte)( ( l >> 8 ) & 0xFF ), (byte)( ( l >> 16 ) & 0xFF ), (byte)( ( l >> 24 ) & 0xFF ) };
      // and if the client is still active
      // then, we write the bytes
      s.write( lengthBytes );
      s.write( jpgBytes );
      // and we go to wait for a new image request
    }
    catch( Exception e ) {
      e.printStackTrace();
    }
  }
}
