import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HashGenerator {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		 if (args.length != 2) {
	            System.out.println("Usage: java -jar DestinationHashGenerator.jar <PRN> <path/to/jsonfile>");
	            return;
	        }

	        String prnNumber = args[0].toLowerCase().replace(" ", "");
	        String jsonFilePath = args[1];

	        try {
	            // Step 2: Read and Parse JSON File
	            ObjectMapper mapper = new ObjectMapper();
	            JsonNode rootNode = mapper.readTree(new File(jsonFilePath));

	            // Step 3: Traverse JSON to find the "destination" key
	            String destinationValue = findDestinationValue(rootNode);
	            if (destinationValue == null) {
	                System.out.println("No 'destination' key found in the JSON file.");
	                return;
	            }

	            // Step 4: Generate a random 8-character alphanumeric string
	            String randomString = generateRandomString(8);

	            // Step 5: Generate the MD5 hash
	            String concatenatedString = prnNumber + destinationValue + randomString;
	            String md5Hash = generateMD5Hash(concatenatedString);

	            // Step 6: Format the output
	            String output = md5Hash + ";" + randomString;
	            System.out.println(output);

	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }

	    // Method to traverse JSON and find the first "destination" key
	    private static String findDestinationValue(JsonNode node) {
	        if (node.isObject()) {
	            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
	            while (fields.hasNext()) {
	                Map.Entry<String, JsonNode> field = fields.next();
	                if (field.getKey().equals("destination")) {
	                    return field.getValue().asText();
	                }
	                String result = findDestinationValue(field.getValue());
	                if (result != null) {
	                    return result;
	                }
	            }
	        } else if (node.isArray()) {
	            for (JsonNode arrayItem : node) {
	                String result = findDestinationValue(arrayItem);
	                if (result != null) {
	                    return result;
	                }
	            }
	        }
	        return null;
	    }

	    // Method to generate an 8-character random alphanumeric string
	    private static String generateRandomString(int length) {
	        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	        Random random = new Random();
	        StringBuilder sb = new StringBuilder();
	        for (int i = 0; i < length; i++) {
	            sb.append(chars.charAt(random.nextInt(chars.length())));
	        }
	        return sb.toString();
	    }

	    // Method to generate MD5 hash
	    private static String generateMD5Hash(String input) {
	        try {
	            MessageDigest md = MessageDigest.getInstance("MD5");
	            byte[] messageDigest = md.digest(input.getBytes());
	            StringBuilder sb = new StringBuilder();
	            for (byte b : messageDigest) {
	                sb.append(String.format("%02x", b));
	            }
	            return sb.toString();
	        } catch (NoSuchAlgorithmException e) {
	            throw new RuntimeException(e);
	        }

	}

}
