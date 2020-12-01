import sys
import os
from matplotlib import pyplot as plt
from imageio import imread,imwrite

from stylize import render


if __name__ == "__main__":
	path = 'C:/Users/omlette/Documents/moving_painting/moving_painting/data/resources/base.png'
	
	# print("Going to go through a few examples using the stylize.render")

	# Load an image into a numpy format and see it
	img = imread(path)


	# print "Please wait, rendering..."
	abstract = render(img,depth=int(sys.argv[1]),verbose=True)
	# show_img(abstract,"A depth of 4 results in an abstract representation")

	# print("Saved results are in the examples directory!")
	imwrite('C:/Users/omlette/Documents/moving_painting/moving_painting/data/example_images/current.png',abstract)