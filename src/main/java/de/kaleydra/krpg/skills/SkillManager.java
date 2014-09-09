package de.kaleydra.krpg.skills;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.annotation.Nonnull;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import de.kaleydra.krpg.KRPG;
import de.kaleydra.krpg.items.KRPGItem;

public class SkillManager {
//	public static final String LEVEL_SEPERATOR = "|";
	
	private Map<String, Class<? extends Skill>> skillClassesByJar = new HashMap<String, Class<? extends Skill>>();
	private Map<String, YamlConfiguration> skillYAMLs = new HashMap<String, YamlConfiguration>();
	// <SkillName+LEVEL_SEPERATOR+currentLevel -> Skill
	private Map<String, Skill[]> skillsWithLevel = new HashMap<String, Skill[]>();
	
	
	private List<URLClassLoader> classLoaders = new LinkedList<URLClassLoader>();
	File skillsFile;
//	YamlConfiguration skillsYML = new YamlConfiguration();
	
	
	KRPG plugin;
	
	public SkillManager(KRPG plugin){
		this.plugin = plugin;
	}
	
	public void closeLoaders(){
		for(URLClassLoader loader: classLoaders){
			try {
				loader.close();
			} catch (IOException e) {
//				e.printStackTrace();
			}
		}
	}
	
//	public Class<? extends Skill> getSkillClassByDisplayName(String displayName){
//		return skillClassesByJar.get(displayName);		
//	}
	
	public void addSkillClass(String skillName, Class<? extends Skill> skillClass, URLClassLoader loader) throws InvalidSkillException{
		
//		final SkillDeclaration skillDeclaration = skillClass.getAnnotation(SkillDeclaration.class);
//		if(skillDeclaration == null) 
//			throw new InvalidSkillException(skillClass.getName()+": Missing SkillDeclaration annotation!");
//		
//		String[] skillNames = skillDeclaration.displayName();
//		if(skillNames == null) 
//			throw new InvalidSkillException(skillClass.getName()+": Missing identifier in SkillDeclaration!");
//		
//		for(String displayName:skillNames) {
//			if(skillClasses.containsKey(displayName)){
//				plugin.getLogger().severe("Error at loading skill "+skillClass.getSimpleName()+"! There is already a skill registered with the displayName "+
//						displayName+", existing class is "+skillClasses.get(displayName).getSimpleName());
//				continue;
//			}
//			skillClasses.put(displayName, skillClass);
//		}
		
		
		try {
			skillClass.getConstructor(SkillSettings.class);
		} catch (NoSuchMethodException e) {
			throw new InvalidSkillException("Missing skill constructor that accepts SkillSettings as a parameter");
		} catch (SecurityException e) {
			e.printStackTrace();
			throw new InvalidSkillException("Security exception! Can't access skill constructor!");
		}
		
		skillClassesByJar.put(formatSkill(skillName), skillClass);
		classLoaders.add(loader);
		
		plugin.getLogger().info("Loaded Skill: "+skillClass.getName()); //("+skillNames+")
	}
	
	/**
	 * @return the skillClasses
	 */
	public Map<String, Class<? extends Skill>> getSkillClassesByJar() {
		return skillClassesByJar;
	}
	/**
	 * @return the classLoaders
	 */
	public List<URLClassLoader> getClassLoaders() {
		return classLoaders;
	}
	
	/**
	 * loads the jars and initializes the skills for each level 
	 * Example Entry in skills.yml:
	 * <pre>
   "SkillJarName":
	  "SkillName":
	    baseSettings:
	      damage: 10
	      range: 4
	    settingsPerLevel:
	    - damage: 10 # level 1 
	    - range: 5 # level 2
	    - destroy: true # level 3 - if there is no base setting, it will fall back to the skill jar settings
	 * </pre>
	 */
	public void load(){
		classLoaders.clear();
		skillClassesByJar.clear();
		skillsWithLevel.clear();
		
		loadSkillJars();
		loadSkillsYaml();
	}
	

	/** 
	 * Load Skills from the /skills directory 
	 * @param skillsFolder Skill directory
	 * */
	@SuppressWarnings("unchecked")
	private void loadSkillJars()
	{
		
		File skillsFolder = new File(plugin.getDataFolder()+"/skills/");
		final Logger logger = plugin.getLogger();
		
		if(!skillsFolder.exists()) {
			skillsFolder.mkdirs();
			logger.info("No skills to load!");
			return;
		}

		
		String foundSkills = "Found skills: ";
		
		boolean first = true;
		for (File skillFile : skillsFolder.listFiles() )
		{			
			String name = skillFile.getName();
			if (!skillFile.isFile() || !name.endsWith(".jar")) continue;
			
			if (!skillFile.canRead())
			{
				logger.severe("Cannot read skill file "+name+"! File permission?");
				continue;
			}
			
			
			if (!first) {
				foundSkills += ", ";
			} 
			first = false;
			
			String skillName = name.replace(".jar", "");
			foundSkills += skillName;		
			
			try {
				// load .jar as zip
				FileInputStream fis = new FileInputStream(skillFile);
				ZipInputStream zis = new ZipInputStream(fis);
				ZipEntry ze = zis.getNextEntry();
				
				// find skill.yml
				while(!ze.getName().equals("skill.yml"))
				{
					ze = zis.getNextEntry();
					if(ze == null){
						zis.close();
						throw new InvalidSkillException("Missing skill.yml in "+skillName+".jar");
					}
				}
				
				
				// read skillinfo.txt
//				BufferedReader bf = new BufferedReader(new InputStreamReader(zis));
				YamlConfiguration yml = new YamlConfiguration();
				
				yml.load(zis);
				skillYAMLs.put(skillName, yml);
				String className = yml.getString("main", "");// bf.readLine();
				if(className == null || className.equals("")) throw new InvalidSkillException("skill.yml is missing main class declaration (main)");
//				bf.close();
				zis.close();
				
				// load class
				URLClassLoader loader = new URLClassLoader(new URL[]{skillFile.toURI().toURL()}, getClass().getClassLoader()); // KRPG.class.getClassLoader
				Class<?> loadedClass = loader.loadClass(className);
//				classLoaders.add(loader);
				
				//loader.close();
				Class<? extends Skill> skillClass = (Class<? extends Skill>) loadedClass;
//				skillClasses.put(skillName, skillClass);
				
				addSkillClass(skillName, skillClass, loader);
				
				
			}catch (Exception e) {
				logger.severe("The skill \""+skillName+" caused an error at loading!");
				if(e instanceof InvalidSkillException) {
					logger.severe(e.getMessage());				
				} else {
					e.printStackTrace();
				}
			}
		}
		
		logger.info(foundSkills);
	}
	
	private void loadSkillsYaml(){
		Logger logger = plugin.getLogger();
		skillsFile = new File(plugin.getDataFolder(), "skills.yml");
		
		if(!skillsFile.exists()) {
			try {
				skillsFile.createNewFile();
			} catch (IOException e) {
				logger.severe("Cannot create "+skillsFile.getName()+"! Stopping loading of Skills!");
				e.printStackTrace();
				return;
			}
		}
		
		// load yaml
		YamlConfiguration skillsYML = new YamlConfiguration();
		try {
			skillsYML.load(skillsFile);
		} catch (Exception e) {
			logger.severe("Cannot load "+skillsFile.getName()+"! Stopping loading of Skills!");
			e.printStackTrace();
			return;
		} 
		
		
		// make sure that every skill.jar has an entry in skills.jar
		for(String skillJarName: skillClassesByJar.keySet()){
			logger.info("Working at "+skillJarName+".jar");
			if(!skillsYML.contains(skillJarName)){
				skillsYML.createSection(skillJarName);
				skillsYML.createSection(skillJarName+".baseSettings");
				skillsYML.createSection(skillJarName+".settingsPerLevel");
//				skillsYML.set(skillJarName, 0); // initialize
				logger.info("Adding basic empty entry to skills.yml for "+skillJarName);
				continue;
			}
			
			ConfigurationSection skillJarSection = skillsYML.getConfigurationSection(skillJarName);
			if(skillJarSection == null){
				logger.info("empty skill.jar section, skipping it!");
				continue;
			}
			for(String skillDisplayName: skillJarSection.getKeys(false)){

				logger.info("Loading Skill "+skillDisplayName);
				try {
					final ConfigurationSection skillsYamlSettings = skillJarSection.getConfigurationSection(skillDisplayName);
					loadSkill(skillDisplayName, skillClassesByJar.get(formatSkill(skillJarName)), skillsYamlSettings, skillYAMLs.get(formatSkill(skillJarName)));
				} catch (InvalidSkillException e) {
					logger.severe("Error at skill loading! Tried to load: "+skillJarName+"."+skillDisplayName);
					logger.severe(e.getMessage());
				}				
			}
		}
		
		// write back to skills.yml
		try {
			skillsYML.save(skillsFile);
		} catch (IOException e) {
			logger.severe("Cannot save "+skillsFile.getName()+"!");
			e.printStackTrace();
		}
	}
	
	private void loadSkill(String skillName, Class<? extends Skill> skillClass, @Nonnull ConfigurationSection skillSection, 
			@Nonnull YamlConfiguration skillYaml) 
			throws InvalidSkillException{
		if(skillYaml == null) 
			plugin.getLogger().severe("skillYaml null!");
		YamlConfiguration tempYaml = new YamlConfiguration();
		
		if(!skillSection.contains("baseSettings")){
			throw new InvalidSkillException("Invalid skills.yml at key: "+skillName+"! Missing baseSettings!");
		}
		ConfigurationSection baseSettings = skillSection.getConfigurationSection("baseSettings");
		for(String path:baseSettings.getKeys(true)) {
			KRPG.getInstance().getLogger().info(path+": " + baseSettings.getString(path));
			tempYaml.set(path, baseSettings.get(path));
		}
		
		if(skillSection.contains("settingsPerLevel")){
 
			ConfigurationSection settingsPerLevel = skillSection.getConfigurationSection("settingsPerLevel"); //TODO test!
			

			// generate base case with level 0
			SkillSettings skillSettings = new SkillSettings(tempYaml.getRoot(), 
					skillYaml.getConfigurationSection("settings"));
			Skill skill;
			try {
				skill = (Skill) skillClass.getConstructor(SkillSettings.class).newInstance(skillSettings);
			} catch (Exception e) {
				plugin.getLogger().severe("Could not load "+skillName+" base case");
				e.printStackTrace();
				return;
			}
			addSkill(skillName, skill, 0);
			
			if(settingsPerLevel == null) {
				plugin.getLogger().info("settingsPerLevel is not existent for "+skillName);
				return;
			}
			// base instance level 0 should always be there!
			int level = 1;
			for(String levelKey: settingsPerLevel.getKeys(false)){
				ConfigurationSection levelSection = settingsPerLevel.getConfigurationSection(levelKey);
				if(level > 0 && levelSection != null){
					for(String pathPerLevel: levelSection.getKeys(true)){
						tempYaml.set(pathPerLevel, levelSection.get(pathPerLevel)); // will automatically override existing keys
					}
				}
				
				// generate SkillSettings for with level
				skillSettings = new SkillSettings(skillYaml.getConfigurationSection("settings"), tempYaml.getDefaultSection());
				try {
					skill = (Skill) skillClass.getConstructor(SkillSettings.class).newInstance(skillSettings);
				} catch (Exception e) {
					plugin.getLogger().severe("Could not load "+skillName+" with level "+level);
					e.printStackTrace();
					level++;
					continue;
				}
				addSkill(skillName, skill, level);
				level++;
			}
		}
		
	}
	private void addSkill(String skillName, Skill skill, int level){
		Skill[] levelArray;
		if(!skillsWithLevel.containsKey(skillName)){
			levelArray = new Skill[level+1];
		} else {
			levelArray = skillsWithLevel.get(skillName);
			if(level >= levelArray.length){
				Skill[] newArray = new Skill[level+1];
				for(int i=0; i < levelArray.length; i++){
					newArray[i] = levelArray[i];
				}
				levelArray = newArray;
			}
		}
		levelArray[level] = skill;
		skillsWithLevel.put(skillName, levelArray);
	}
	
	private String formatSkill(String skillJarName){
		return skillJarName.replace('.', '_');
	}
	
	
	
	public boolean applySkillToItem(KRPGItem krpgItem, String skillDisplayName, int currentLevel){
//		Class<? extends Skill> skillClass = KRPG.getInstance().getSkillManager().getSkillClassByDisplayName(skillDisplayName);
//		if (skillClass == null) {
//			KRPG.logInfo("unknown skill: " + skillDisplayName);
//			return;
//		}
//		Skill skill;
//		try {
//			skill = (Skill) skillClass.getConstructor().newInstance();
//		} catch (Exception e) {
//			e.printStackTrace();
//			return;
//		}
		Skill skill = getSkill(skillDisplayName, currentLevel);
		if(skill == null) return false;
		
		skill.setKRPGItem(krpgItem);
		skill.setIdentifier(skillDisplayName);
		skill.setCurrentLevel(currentLevel);
		
//		skill.setCurrentLevel(currentLevel);
//		skill.setCurrentDisplayName(skillDisplayName);
		krpgItem.addSkill(skill);
//		return skill;
		return true;
	}
	
	public Skill getSkill(String skillDisplayName, int currentLevel){
		Skill[] levelArray = skillsWithLevel.get(skillDisplayName);
		Skill skill = null;
		if(!skillsWithLevel.containsKey(skillDisplayName)) return null;
		if(currentLevel >= levelArray.length || levelArray[currentLevel] == null){
			for(int i = currentLevel; i >= 0; i--){
				if(i >= levelArray.length) continue;
				skill = levelArray[i];
				if(skill != null) break;
			}
		} else {
			skill = levelArray[currentLevel];
		}
		
		if(skill == null) {
			plugin.getLogger().warning("Could not find skill: "+skillDisplayName+" with level "+currentLevel);
			return null;
		}
		
//		skill.setKRPGItem(krpgItem);
		skill.setIdentifier(skillDisplayName);
		skill.setCurrentLevel(currentLevel);
//		plugin.getLogger().info("Found skill: "+skillDisplayName+" with level "+currentLevel);
		return skill.clone();
	}
	
	
	
}
