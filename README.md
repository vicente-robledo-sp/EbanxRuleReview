# Email Generator Harness

This repository contains a standalone Java harness that mimics the original SailPoint email generation rule so you can exercise every branch locally.

## Running the harness

Use the helper script to compile and run the harness. With no arguments it starts the interactive prompt so you can type multiple names in one session:

```bash
./run_email_harness.sh
```

You can also pass a name and type ("employee" or "contractor") to perform a single run and exit:

```bash
./run_email_harness.sh "Joao Tuck Silva" employee
```

Every generated prefix is stored in memory for the duration of the process, so repeated names let you test the collision handling logic. Edit `EmailHarnessApp` if you want to seed the harness with additional reserved prefixes before running the CLI or to adjust the default prompts.
