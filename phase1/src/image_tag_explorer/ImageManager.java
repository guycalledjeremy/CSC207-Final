package image_tag_explorer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImageManager {
    public List<Image> images;

    public ImageManager() {
        this(new File[0]);
    }

    public ImageManager(File[] files) {
        this.images = new ArrayList<>(files.length);
        this.addImages(files);
        //TODO: figure out how this constructor can just call the one below, passing in the right info.
    }

    public ImageManager(Image[] images) {
        this.images = new ArrayList<>(Arrays.asList(images));
    }

    public boolean addImage(Image newImage) {
        return images.add(newImage);
    }

    public boolean addImages(File[] files) {
    	boolean ret = true;
		for(int i = 0; i<files.length; i++ ) {
			if(!images.add(new Image(files[i]))) {
				ret = false;
			}
		}
		return ret;
	}

    public boolean removeImage(Image image) {
        return images.remove(image);
    }

    public Image[] getImages() {
        return (Image[]) images.toArray();
    }

}
