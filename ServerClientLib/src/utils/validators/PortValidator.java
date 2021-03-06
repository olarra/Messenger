/**
 * Copyright 2017
 *
 *
 * Sous licence Apache, Version 2.0 (la "Licence");
 * Vous ne pouvez pas utiliser ce fichier sauf en conformité avec la licence.
 * Vous pouvez obtenir une copie de la licence à l'adresse :
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Sauf si requis par la loi en vigueur ou accord écrit, le logiciel
 * Distribué sous licence est distribué «TEL QUEL»,
 * SANS GARANTIES OU CONDITIONS D'AUCUNE SORTE, express ou implicite.
 * Voir la licence pour les autorisations spécifiques aux différentes langues et
 * Limitations sous la licence.
 *
 * Contribué par : Jesus GARNICA OLARRA, Hivinau GRAFFE
 */

package utils.validators;

/**
 * 
 * @author Hivinau GRAFFE
 */
public class PortValidator extends Validator {

	 private static final String DEFAULT_REGEX = "^[0-9]+$";
	    
	 public PortValidator() {
	        
		 this(PortValidator.DEFAULT_REGEX);
	 }
	    
	 public PortValidator(String regex) {
	        
		 super(regex);
	 }
	 
	@Override
	public boolean validate(String content) {
		
		if(super.validate(content)) {
			
			try {
				
				return Integer.parseInt(content) < 65536;
				
			} catch(Exception ignored) {
				
				return false;
			}
		}
		
		return false;
	}
}
