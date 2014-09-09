package de.kaleydra.krpg.skills;

public class MissingSkillSettingsKeyException extends RuntimeException {

	private static final long serialVersionUID = 57028955280169L;
	
	public MissingSkillSettingsKeyException(String missingKey) {
		super("There is no skill setting for the key \""+missingKey+"\"");
	}
}
