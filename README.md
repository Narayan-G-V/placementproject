# Banking and Transaction System - Team B

A Java console application for account creation, secure deposits and withdrawals, account transfers, and mini-statements. It uses only the Java standard library and keeps its demo data in memory for the current run.

## Requirements

- JDK 8 or newer
- A terminal with PowerShell (the commands below are for Windows)

## Build and run

From the project root:

```powershell
$sources = Get-ChildItem -Path 'src\main\java' -Recurse -Filter '*.java' | ForEach-Object { $_.FullName }
New-Item -ItemType Directory -Force -Path 'bin' | Out-Null
javac -Xlint:all -d 'bin' $sources
java -cp 'bin' app.Main
```

The application starts with two demo accounts:

| Account | Type | PIN |
| --- | --- | --- |
| `ACC1001` | Savings | `1234` |
| `ACC1002` | Current | `5678` |

PIN verification is required to view account details and mini-statements and to make deposits, withdrawals, and transfers. New accounts and transaction history are held in memory and reset when the application exits. Savings accounts require a $500 minimum balance; current accounts can use their configured overdraft limit. Amounts are handled as decimal currency and limited to two decimal places.

## Run the regression checks

The self-tests use no third-party libraries:

```powershell
$sources = Get-ChildItem -Path 'src\main\java','src\test\java' -Recurse -Filter '*.java' | ForEach-Object { $_.FullName }
New-Item -ItemType Directory -Force -Path 'bin' | Out-Null
javac -Xlint:all -d 'bin' $sources
java -cp 'bin' tests.BankingSystemSelfTest
```

## Project requirements demonstrated

- **OOP:** `Account` defines shared account behavior; `SavingsAccount` and `CurrentAccount` implement different withdrawal rules. Private fields and PIN validation provide encapsulation.
- **Custom exceptions:** account lookup, invalid operations, and insufficient funds use dedicated exception types and are handled at the console boundary.
- **Custom DSA:** `TransactionStack` is a linked, fixed-capacity stack used to retain the five newest mini-statement entries.
- **Collections:** `AccountService` and `StatementService` use `HashMap` for account lookup and per-account transaction history.
- **Console UI:** `Main` uses `Scanner` input and a text menu; there is no GUI or database.

See [docs/COMMIT_GUIDE.md](docs/COMMIT_GUIDE.md) for the team's suggested contribution and commit sequence. Team members should make genuine, incremental commits as they contribute and ensure every member can explain the complete project.
