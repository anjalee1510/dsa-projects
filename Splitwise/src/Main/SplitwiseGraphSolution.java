package Main;
import java.util.*;

public class SplitwiseGraphSolution {
    public int minTransfers(int[][] transactions) {
        // Step 1: Calculate net balances for each person
        Map<Integer, Integer> balanceMap = new HashMap<>();
        for (int[] transaction : transactions) {
            int from = transaction[0];
            int to = transaction[1];
            int amount = transaction[2];
            balanceMap.put(from, balanceMap.getOrDefault(from, 0) - amount);
            balanceMap.put(to, balanceMap.getOrDefault(to, 0) + amount);
        }

        // Step 2: Extract non-zero balances
        List<Integer> balances = new ArrayList<>();
        List<Integer> persons = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : balanceMap.entrySet()) {
            if (entry.getValue() != 0) {
                persons.add(entry.getKey());
                balances.add(entry.getValue());
            }
        }

        // Step 3: Use greedy matching to settle debts
        List<String> transactionsList = new ArrayList<>();
        int minTransactions = settleDebts(balances, persons, transactionsList);

        // Print final transactions
        System.out.println("Transactions to settle debts:");
        if (transactionsList.isEmpty()) {
            System.out.println("No transactions required as all balances are already settled.");
        } else {
            for (String transaction : transactionsList) {
                System.out.println(transaction);
            }
        }
        return minTransactions;
    }

    private int settleDebts(List<Integer> balances, List<Integer> persons, List<String> transactionsList) {
        PriorityQueue<Node> creditors = new PriorityQueue<>((a, b) -> b.balance - a.balance); // Max-Heap
        PriorityQueue<Node> debtors = new PriorityQueue<>((a, b) -> a.balance - b.balance);   // Min-Heap

        // Split creditors and debtors
        for (int i = 0; i < balances.size(); i++) {
            int balance = balances.get(i);
            if (balance > 0) creditors.add(new Node(persons.get(i), balance));
            if (balance < 0) debtors.add(new Node(persons.get(i), balance));
        }

        int transactionCount = 0;

        // Match creditors and debtors
        while (!creditors.isEmpty() && !debtors.isEmpty()) {
            Node creditor = creditors.poll();
            Node debtor = debtors.poll();

            // Determine the settlement amount
            int settlement = Math.min(creditor.balance, -debtor.balance);

            // Record the transaction
            transactionsList.add("Person " + debtor.id + " pays Person " + creditor.id + " amount " + settlement);

            // Update balances
            creditor.balance -= settlement;
            debtor.balance += settlement;

            // Re-add to heaps if still owed
            if (creditor.balance > 0) creditors.add(creditor);
            if (debtor.balance < 0) debtors.add(debtor);

            transactionCount++;
        }

        return transactionCount;
    }

    // Helper class for heap nodes
    static class Node {
        int id;      // Person ID
        int balance; // Amount owed or credited

        Node(int id, int balance) {
            this.id = id;
            this.balance = balance;
        }
    }

    public static void main(String[] args) {
        SplitwiseGraphSolution solution = new SplitwiseGraphSolution();

        int[][] transactions = {
            {0, 1, 10},
            {2, 0, 5},
            {1, 2, 5},
            {1, 3, 10}
        };

        int result = solution.minTransfers(transactions);
        System.out.println("Minimum number of transactions required to settle debts: " + result);
    }
}
