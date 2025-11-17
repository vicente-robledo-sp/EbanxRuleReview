import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 * Small self-contained harness that mirrors the original SailPoint rule logic
 * so you can exercise every branch locally.
 */
public class EmailGeneratorHarness {
    private final Set<String> existingPrefixes;

    public EmailGeneratorHarness(Set<String> existingPrefixes) {
        this.existingPrefixes = new HashSet<>();
        for (String prefix : existingPrefixes) {
            if (prefix != null) {
                this.existingPrefixes.add(prefix.toLowerCase());
            }
        }
    }

    public String removerAccent(String str) {
        return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }

    public String removePreposition(String str) {
        String defaultPattern = "(\\w)(\\s+)(e|do|da|do|das|dos|de|di|du)(\\s+)(\\w)";
        return str.replaceAll(defaultPattern, "$1 $5");
    }

    public String removeAgnome(String name) {
        String[] nomeTrat = name.split(" ");
        List<String> agnomes = new ArrayList<>();
        agnomes.add("filho");
        agnomes.add("junior");
        agnomes.add("neto");
        agnomes.add("sobrinho");
        agnomes.add("jr");
        agnomes.add("filha");
        agnomes.add("neta");

        for (int x = 0; x < nomeTrat.length; x++) {
            if (agnomes.contains(nomeTrat[x])) {
                name = name.replaceAll(nomeTrat[x], "");
            }
        }

        return name;
    }

    private boolean isUnique(String value) {
        return !existingPrefixes.contains(value.toLowerCase());
    }

    public void reservePrefix(String value) {
        if (value == null) {
            return;
        }
        existingPrefixes.add(value.toLowerCase());
    }

    public String generateUniqueEmail(String displayName, String type) {
        if (displayName == null || displayName.isEmpty()) {
            return "";
        }

        String cleanDisplayName = displayName.trim().replaceAll(" +", " ");
        String[] arrayName = cleanDisplayName.split(" ");
        int count = arrayName.length;
        String firstName = arrayName[0];
        String lastName = arrayName[count - 1];
        String secondName = null;
        String penultimateName = null;
        String suggestedUserName = "";
        String loginSmall = firstName;
        String ext = "ext";

        if (count > 2) {
            secondName = arrayName[1];
            penultimateName = arrayName[count - 2];
        }

        if (type.equalsIgnoreCase("employee")) {
            suggestedUserName = firstName + "." + lastName;
        } else if (type.equalsIgnoreCase("contractor")) {
            suggestedUserName = ext + "." + firstName + "." + lastName;
        }

        if (suggestedUserName.length() <= 18) {
            loginSmall = suggestedUserName;
        }

        if (suggestedUserName.length() <= 20 && isUnique(suggestedUserName)) {
            return suggestedUserName;
        }

        if (count > 2) {
            if (type.equalsIgnoreCase("employee")) {
                suggestedUserName = firstName + "." + penultimateName;
            } else if (type.equalsIgnoreCase("contractor")) {
                suggestedUserName = ext + "." + firstName + "." + penultimateName;
            }
            if (suggestedUserName.length() <= 18) {
                loginSmall = suggestedUserName;
            }
            if (suggestedUserName.length() <= 20 && isUnique(suggestedUserName)) {
                return suggestedUserName;
            }
        }

        if (count > 2) {
            if (type.equalsIgnoreCase("employee")) {
                suggestedUserName = lastName + "." + firstName;
            } else if (type.equalsIgnoreCase("contractor")) {
                suggestedUserName = ext + "." + lastName + "." + firstName;
            }
            if (suggestedUserName.length() <= 18) {
                loginSmall = suggestedUserName;
            }
            if (suggestedUserName.length() <= 20 && isUnique(suggestedUserName)) {
                return suggestedUserName;
            }
        }

        if (count > 2) {
            if (type.equalsIgnoreCase("employee")) {
                suggestedUserName = firstName + "." + secondName.charAt(0) + "." + lastName;
            } else if (type.equalsIgnoreCase("contractor")) {
                suggestedUserName = ext + "." + firstName + "." + secondName.charAt(0) + "." + lastName;
            }
            if (suggestedUserName.length() <= 18) {
                loginSmall = suggestedUserName;
            }
            if (suggestedUserName.length() <= 20 && isUnique(suggestedUserName)) {
                return suggestedUserName;
            }
        }

        if (count > 2) {
            if (type.equalsIgnoreCase("employee")) {
                suggestedUserName = firstName + "." + secondName.charAt(0) + lastName;
            } else if (type.equalsIgnoreCase("contractor")) {
                suggestedUserName = ext + "." + firstName + "." + secondName.charAt(0) + lastName;
            }
            if (suggestedUserName.length() <= 18) {
                loginSmall = suggestedUserName;
            }
            if (suggestedUserName.length() <= 20 && isUnique(suggestedUserName)) {
                return suggestedUserName;
            }
        }

        String resetedLoginSmall = "";
        for (int x = 1; x < 100; x++) {
            if (type.equalsIgnoreCase("employee")) {
                resetedLoginSmall = firstName + "." + lastName;
            } else if (type.equalsIgnoreCase("contractor")) {
                resetedLoginSmall = ext + "." + firstName + "." + lastName;
                loginSmall = ext + "." + firstName;
            }

            if (resetedLoginSmall.length() <= 18) {
                loginSmall = resetedLoginSmall;
            }

            suggestedUserName = loginSmall;
            String suggestedUserNameCount = suggestedUserName + x;
            if (isUnique(suggestedUserNameCount)) {
                suggestedUserName = suggestedUserNameCount;
                break;
            }
        }

        return suggestedUserName;
    }

    private String prepareName(String displayName) {
        String formatted = displayName.toLowerCase();
        formatted = removeAgnome(formatted);
        formatted = removePreposition(formatted);
        return removerAccent(formatted);
    }

    public static void main(String[] args) {
        Set<String> reserved = new HashSet<>(Arrays.asList(
                "joao.silva",
                "joao.tuck",
                "silva.joao",
                "ext.joao.silva",
                "ext.joao.tuck"
        ));

        EmailGeneratorHarness harness = new EmailGeneratorHarness(reserved);
        List<String> generatedEmails = new ArrayList<>();

        if (args.length >= 2) {
            runSingleInvocation(harness, generatedEmails, args[0], args[1]);
            return;
        }

        runInteractiveSession(harness, generatedEmails);
    }

    private static void runSingleInvocation(EmailGeneratorHarness harness, List<String> generatedEmails,
                                            String displayName, String type) {
        String preparedName = harness.prepareName(displayName);
        String prefix = harness.generateUniqueEmail(preparedName, type);
        harness.reservePrefix(prefix);
        generatedEmails.add(prefix + "@example.com");
        System.out.println("Result: " + prefix);
        System.out.println("Stored emails: " + generatedEmails);
    }

    private static void runInteractiveSession(EmailGeneratorHarness harness, List<String> generatedEmails) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Interactive email generator");
            System.out.println("Existing test values: " + harness.existingPrefixes);
            System.out.println("Enter a blank line for the name to exit.\n");

            while (true) {
                System.out.print("Full name: ");
                if (!scanner.hasNextLine()) {
                    break;
                }
                String rawName = scanner.nextLine().trim();
                if (rawName.isEmpty()) {
                    break;
                }

                System.out.print("Type (employee/contractor, default employee): ");
                String typeInput = scanner.hasNextLine() ? scanner.nextLine().trim() : "";
                if (typeInput.isEmpty()) {
                    typeInput = "employee";
                }

                String preparedName = harness.prepareName(rawName);
                String prefix = harness.generateUniqueEmail(preparedName, typeInput);
                harness.reservePrefix(prefix);
                String email = prefix + "@example.com";
                generatedEmails.add(email);

                System.out.printf("Generated email: %s%n", email);
                System.out.println("All generated emails: " + generatedEmails);
                System.out.println();
            }

            if (generatedEmails.isEmpty()) {
                System.out.println("No emails were generated in this session.");
            } else {
                System.out.println("Session summary:");
                for (int i = 0; i < generatedEmails.size(); i++) {
                    System.out.printf("%2d) %s%n", i + 1, generatedEmails.get(i));
                }
            }
        }
    }
}
