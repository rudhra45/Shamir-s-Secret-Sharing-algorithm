import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class ShamirSecretSharingExam {

    // Helper method to convert the base of a given value to decimal
    public static BigInteger decodeValue(String base, String value) {
        int baseInt = Integer.parseInt(base);
        return new BigInteger(value, baseInt);
    }

    // Lagrange Interpolation to find the value of the polynomial at a given x
    public static BigInteger lagrangeInterpolationAtX(List<BigInteger> xVals, List<BigInteger> yVals, BigInteger x) {
        BigInteger result = BigInteger.ZERO;
        int n = xVals.size();

        for (int i = 0; i < n; i++) {
            BigInteger term = yVals.get(i);
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    BigInteger numerator = x.subtract(xVals.get(j));
                    BigInteger denominator = xVals.get(i).subtract(xVals.get(j));
                    term = term.multiply(numerator).divide(denominator);
                }
            }
            result = result.add(term);
        }

        return result;
    }

    // Method to parse and decode the input from JSON file
    public static List<BigInteger[]> parseInput(JSONObject json) {
        List<BigInteger[]> points = new ArrayList<>();
        JSONObject keys = json.getJSONObject("keys");
        int n = keys.getInt("n");

        for (int i = 1; i <= n; i++) {
            if (json.has(String.valueOf(i))) {
                JSONObject root = json.getJSONObject(String.valueOf(i));
                BigInteger x = BigInteger.valueOf(i);
                BigInteger y = decodeValue(root.getString("base"), root.getString("value"));
                points.add(new BigInteger[] { x, y });
            }
        }

        return points;
    }

    public static void main(String[] args) {
        try {
            // First test case
            FileInputStream inputStream1 = new FileInputStream("test1.json");
            JSONTokener tokener1 = new JSONTokener(inputStream1);
            JSONObject json1 = new JSONObject(tokener1);

            // Second test case
            FileInputStream inputStream2 = new FileInputStream("test2.json");
            JSONTokener tokener2 = new JSONTokener(inputStream2);
            JSONObject json2 = new JSONObject(tokener2);

            // Parse and decode first test case
            List<BigInteger[]> points1 = parseInput(json1);
            List<BigInteger> xVals1 = new ArrayList<>();
            List<BigInteger> yVals1 = new ArrayList<>();

            for (BigInteger[] point : points1) {
                xVals1.add(point[0]);
                yVals1.add(point[1]);
            }

            // Perform Lagrange interpolation to find the constant term (secret) for the
            // first test case
            BigInteger secret1 = lagrangeInterpolationAtX(xVals1, yVals1, BigInteger.ZERO);
            System.out.println("Secret (Test Case 1): " + secret1);

            // Parse and decode second test case
            List<BigInteger[]> points2 = parseInput(json2);
            List<BigInteger> xVals2 = new ArrayList<>();
            List<BigInteger> yVals2 = new ArrayList<>();

            for (BigInteger[] point : points2) {
                xVals2.add(point[0]);
                yVals2.add(point[1]);
            }

            // Use the first 'k' points to interpolate and find the polynomial
            JSONObject keys2 = json2.getJSONObject("keys");
            int k = keys2.getInt("k");

            List<BigInteger> xValsForInterpolation = xVals2.subList(0, k);
            List<BigInteger> yValsForInterpolation = yVals2.subList(0, k);

            // Calculate the secret (constant term) for second test case
            BigInteger secret2 = lagrangeInterpolationAtX(xValsForInterpolation, yValsForInterpolation,
                    BigInteger.ZERO);
            System.out.println("Secret (Test Case 2): " + secret2);

            // Check for wrong points in the second test case
            List<Integer> wrongPoints = new ArrayList<>();

            for (int i = 0; i < xVals2.size(); i++) {
                BigInteger calculatedY = lagrangeInterpolationAtX(xValsForInterpolation, yValsForInterpolation,
                        xVals2.get(i));
                if (!calculatedY.equals(yVals2.get(i))) {
                    wrongPoints.add(i + 1); // x-values start from 1
                }
            }

            if (wrongPoints.isEmpty()) {
                System.out.println("No wrong points found in Test Case 2.");
            } else {
                System.out.println("Wrong points in Test Case 2: " + wrongPoints);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}