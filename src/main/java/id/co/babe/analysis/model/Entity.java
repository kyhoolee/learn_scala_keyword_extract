package id.co.babe.analysis.model;

public class Entity {
	public static final int type_person = 1;
	public static final int type_orginization = 2;
	public static final int type_event = 3;
	public static final int type_place = 4;
	public static final int type_unknow = 0;
			
	
	
	
	public String name;
	public int occFreq;
	public double score;
	public int entityType;

	public Entity() {

	}
	
	public Entity(String name, int occFreq, double score, int entityType) {
		this.name = name;
		this.occFreq = occFreq;
		this.score = score;
		this.entityType = entityType;
	}
	
	public Entity(String name, int occFreq, int entityType) {
		this.name = name;
		this.occFreq = occFreq;
		this.score = 0.0;
		this.entityType = entityType;
	}

	public String toString() {
		return name + " -- " + occFreq + " -- " + score + " -- " + entityType;
	}

}
