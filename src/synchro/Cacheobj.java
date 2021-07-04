package synchro;

import java.io.Serializable;

public class Cacheobj implements Serializable {
	
	public String path;
	public long size;
	public boolean isdir;

	public Cacheobj(String path, Long size, boolean isdir) {
		
		this.path = path;
		this.size = size;
		this.isdir = isdir;		
	}

}
