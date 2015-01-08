package Client;

public enum FileSize {

	b(0),
	Kb(1),
	MB(2),
	GB(3),
	TB(4);
	
	int index;
	FileSize(int i){
		this.index = i;
	}
}
