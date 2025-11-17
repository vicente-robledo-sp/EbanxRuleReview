import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 * Entry point class that wires the EmailGeneratorHarness into a simple CLI so it can
 * be executed locally with or without arguments.
 */
public final class EmailHarnessApp {
    private EmailHarnessApp() {
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
            System.out.println("Existing test values: " + harness.getExistingPrefixes());
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
