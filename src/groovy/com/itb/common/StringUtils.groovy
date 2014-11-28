package com.itb.common

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

import java.util.regex.Pattern

/**
 *
 */

public class StringUtils {

    private static final Log log = LogFactory.getLog(StringUtils.class)

    static String maskNumber(String str, int noOfUnmaskedCharsAtEnd = 4) {
        int len = str?.length() - noOfUnmaskedCharsAtEnd
        if(!len || len < 0)
            return ''
        String ret = ''
        len.times {ret += '*'}
        ret += str.substring(len)
        return ret
    }

    static String truncate(String str, int maxLength) {
        if(!str)
            return str
        int len = Math.min(str.length(), maxLength)
        return str.substring(0, len)
    }

    /**
    * Used for string -> BigDecimal conversion with skipping of possible errors.
     */
    static BigDecimal getNumber(val){
        BigDecimal result = null
        if(val != null){
            if(val instanceof BigDecimal) {
                return val
            }
            try {
                result = new BigDecimal(val)
            }
            catch (ex) {
                try {
                    if(val && val instanceof String) {
                        // let's remove dollar sign and comma and then try again
                        String s1 = val.replace("\$", "")
                        String s2 = s1.replace(",", "")
                        result = new BigDecimal(s2?.trim())
                    }
                }catch (Exception e){}
            }
        }
        return result
    }


    /**
     * Given a CSV string, it returns a list of strings after trimming whitespace, escaping quoted values, etc.
     * This function correctly handles escaped commas ("Doe, John", "Dallas, TX")
     * The fields are trimmed for leading/trailing double-quote or whitespace characters.
     * @param input The CSV string with optional quoted fields
     * @return The list of fields
     */
    static List<String> parseCsvLine(String input) {
        List<String> result = [];
        int start = 0;
        boolean inQuotes = false;

        def stripSpaceAndQuote = { String field ->
            //println "Field=[$field]"
            int len = field.length();
            if(!len)
                return field;
            int i=0, j=len-1;
            while(i < len && (field.charAt(i) == ' ' || field.charAt(i) == '\"') )
                i++;
            while(j >= 1 && (field.charAt(j) == ' ' || field.charAt(j) == '\"') )
                j--;
            //println "Field=[$field], i=$i, j=$j"
            if(j < 0 || j < i) // check if the field is a series of consecutive spaces
                return ''
            //println "TrimmedField=[${field.substring(i, j+1)}]"
            return field.substring(i, j+1)
        }


        for (int current = 0; current < input.length(); current++) {
            if (input.charAt(current) == '\"')
                inQuotes = !inQuotes; // toggle state
            boolean atLastChar = (current == input.length() - 1);
            //println "atLastChar=$atLastChar, start=$start, current=$current, X=${input.charAt(current)}"
            if(atLastChar) {
                if (input.charAt(current) == ',') // ends with ,
                    result.add(stripSpaceAndQuote(input.substring(start, current)));
                else
                    result.add(stripSpaceAndQuote(input.substring(start)));
            }
            else if (input.charAt(current) == ',' && !inQuotes) {
                result.add(stripSpaceAndQuote(input.substring(start, current)));
                start = current + 1;
            }
        }
        return result;
    }


    /**  It will parse the camelCasing to human readable names
     *   Input                  Output
     *   "lowercase",        // [lowercase]
     *   "Class",            // [Class]
     *   "MyClass",          // [My Class]
     *   "HTML",             // [HTML]
     *   "PDFLoader",        // [PDF Loader]
     *   "AString",          // [A String]
     *   "SimpleXMLParser",  // [Simple XML Parser]
     *   "GL11Version",      // [GL 11 Version]
     *   "99Bottles",        // [99 Bottles]
     *   "May5",             // [May 5]
     *   "BFG9000",          // [BFG 9000]
     *   Work for all these type of pattern
     *
     * */
    static String splitCamelCase(String s) {
        return s.replaceAll(
                String.format("%s|%s|%s",
                        "(?<=[A-Z])(?=[A-Z][a-z])",
                        "(?<=[^A-Z])(?=[A-Z])",
                        "(?<=[A-Za-z])(?=[^A-Za-z])"
                ),
                " "
        );
    }

    /**
     * This function replaces all ', <, and > characters (known to cause SqlInjection and cross site scripting hacks)
     */
    static String removeScriptletAndSQLInjectionChars(String s, boolean removeSql = true){
        if(s) {
            if(s.contains("'") && removeSql)
                s = s.replaceAll("'","");
            if(s.contains("<"))
                s = s.replaceAll("<","")
            if(s.contains(">"))
                s = s.replaceAll(">","")
        }
        return s;
    }

    public static String stripXSS(String value) {
		if (value != null) {
			// avoid encoded attacks.
			// Avoid null characters
			value = value.replaceAll("\0", "");

			// Avoid anything between script tags
			Pattern scriptPattern = Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE);
			value = scriptPattern.matcher(value).replaceAll("");

			// avoid iframes
			scriptPattern = Pattern.compile("<iframe(.*?)>(.*?)</iframe>", Pattern.CASE_INSENSITIVE);
			value = scriptPattern.matcher(value).replaceAll("");

			// Avoid anything in a src='...' type of expression
			scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
			value = scriptPattern.matcher(value).replaceAll("");

			scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
			value = scriptPattern.matcher(value).replaceAll("");

			scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*([^>]+)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
			value = scriptPattern.matcher(value).replaceAll("");

			// Remove any lonesome </script> tag
			scriptPattern = Pattern.compile("</script>", Pattern.CASE_INSENSITIVE);
			value = scriptPattern.matcher(value).replaceAll("");

			// Remove any lonesome <script ...> tag
			scriptPattern = Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
			value = scriptPattern.matcher(value).replaceAll("");

			// Avoid eval(...) expressions
			scriptPattern = Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
			value = scriptPattern.matcher(value).replaceAll("");

			// Avoid expression(...) expressions
			scriptPattern = Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
			value = scriptPattern.matcher(value).replaceAll("");

			// Avoid javascript:... expressions
			scriptPattern = Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE);
			value = scriptPattern.matcher(value).replaceAll("");

			// Avoid vbscript:... expressions
			scriptPattern = Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE);
			value = scriptPattern.matcher(value).replaceAll("");

			// Avoid onload= expressions
			scriptPattern = Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
			value = scriptPattern.matcher(value).replaceAll("");
		}
		return value;
	}

    /**
     * Returns the list of friendly error messages when validation fails on a Grails Domain Object
     * @param messageSource Define this in a service or controller with "def messageSource"
     * @param domainObject The domain object that has errors
     * @return A CSV list of error messages
     */
    static String getErrorMessage(messageSource, domainObject) {
        List<String> errorList = []
        if(domainObject?.errors?.allErrors) {
            domainObject.errors.allErrors.each {
                String s = it.toString()
                String err = messageSource.getMessage(it, Locale.default)
                if(err)
                    errorList.add(err)
                else
                    errorList.add(s.substring(0, s.indexOf(';')) )
            }
        }
        return !errorList.isEmpty() ? errorList.toString() : ""
    }

    static Long toLong(String val){
        Long id = null
        try {
            id = val.toLong();
        } catch (Exception ex) {
            return null
        }
        return id
    }

    static BigDecimal toBigDecimal(String val){
        BigDecimal value = null
        try {
            value = val.toBigDecimal();
        } catch (Exception ex) {
            return null
        }
        return value
    }
    static Double toDouble(String val){
        Double value = null
        try {
            value = val.toDouble();
        } catch (Exception ex) {
            return null
        }
        return value
    }
    static Integer toInteger(String val){
        Integer value = null
        try {
            value = val.toInteger();
        } catch (Exception ex) {
            return null
        }
        return value
    }

    static Boolean toBoolean(String val){
        Boolean value
        try {
            value = val.equalsIgnoreCase("true") || val.equalsIgnoreCase("success") || val.equalsIgnoreCase("Yes")
        } catch (Exception ex) {
            return null
        }
        return value
    }

    static def typeCasting(String val, String type){
        if (val) {
            if (type.equalsIgnoreCase("String")) {
                return val
            } else if (type.equalsIgnoreCase("Date")) {
                return DateUtils.parseDate(val)
            } else {
                type = "to${type}"
                return StringUtils."${type}"(val)
            }
        }
        return null
    }
    
    public static String emptyFiller(Integer length){
        String blank = ''
        while (length > 0) {
            blank = blank + ' '
            length--
        }
        return blank
    }

    public static Map toMap(String value){
        Map map = [:]
        try {
            if (!value?.contains(":")) {
                return null
            }
            String[] keysValues
            if (value?.contains(",")) {
                keysValues = value?.split(",")
                keysValues?.each {String keyValueString ->
                    if (keyValueString?.contains(":")) {
                        String[] keyValuePair = keyValueString?.split(":");
                        if (keyValuePair?.size() == 2) {
                            map[keyValuePair[0]] = keyValuePair[1]
                        }
                    }
                }
                return map
            } else {
                keysValues = value?.split(":")
                if (keysValues?.size() == 2) {
                    map[keysValues[0]] = keysValues[1]
                }
                return map
            }
        } catch (Exception ex) {
            return null
        }
    }


}