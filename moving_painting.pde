
import processing.video.*;

Capture cam;
Command cmd;
PImage photo;
int time;

static final String APP = "python ";
static final String FILE = "C:/Users/omlette/Documents/moving_painting/moving_painting/data/painting.py ";

void setup() {
  size(640, 480);
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
}


void draw() {
  if (cam.available() == true) {
    cam.read();
  }


  int currtime = second() - time;
  image(photo, 0, 0);

  if (currtime % 2 == 0) {
    cam.save("data/resources/base.png");
    cmd = new Command(APP+FILE+8);
    println(cmd.command, ENTER);

    cmd.run();
    photo=loadImage("data/example_images/current.png");
  }
}
