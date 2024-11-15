package Main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SplitwiseSolution {
    public int minTransfers(int[][] transactions) {
        // Step 1: Calculate net balance for each person
        Map<Integer, Integer> balanceMap = new HashMap<>();
        for (int[] transaction : transactions) {
            int from = transaction[0];
            int to = transaction[1];
            int amount = transaction[2];
            balanceMap.put(from, balanceMap.getOrDefault(from, 0) - amount);
            balanceMap.put(to, balanceMap.getOrDefault(to, 0) + amount);
        }
        //Map[(0,-5),(1,-5),(2,0),(3,10)]
        for (Map.Entry<Integer, Integer> entry : balanceMap.entrySet()) {
        	System.out.println("Person: "+entry.getKey()+" amount: "+entry.getValue());
        }
        

        // Step 2: Store all non-zero balances in a list and maintain person IDs
        List<Integer> balances = new ArrayList<>();
        List<Integer> persons = new ArrayList<>(); // Keeps track of each person's ID

        for (Map.Entry<Integer, Integer> entry : balanceMap.entrySet()) {
            int person = entry.getKey();
            int balance = entry.getValue();
            if (balance != 0) {
                persons.add(person); // Store the person's ID
                balances.add(balance); // Store the person's balance
            }
        }

        // Step 3: Initialize transaction lists and find minimum transactions
        List<String> currentTransactionsList = new ArrayList<>();
        List<String> finalTransactionsList = new ArrayList<>(); // Stores the final result transactions
        int minTransactions = settleDebts(balances, persons, 0, currentTransactionsList, finalTransactionsList);

        // Print transaction details
        System.out.println("Transactions to settle debts:");
        if (finalTransactionsList.isEmpty()) {
            System.out.println("No transactions required as all balances are already settled.");
        } else {
            for (String transaction : finalTransactionsList) {
                System.out.println(transaction);
            }
        }

        return minTransactions;
    }

    private int settleDebts(List<Integer> balances, List<Integer> persons, int start, 
                            List<String> currentTransactionsList, List<String> finalTransactionsList) {
        // Skip settled debts
        while (start < balances.size() && balances.get(start) == 0) {
            start++;
        }
        if (start == balances.size()) {
            // Found a minimal set of transactions; update finalTransactionsList
            if (finalTransactionsList.isEmpty() || currentTransactionsList.size() < finalTransactionsList.size()) {
                finalTransactionsList.clear();
                finalTransactionsList.addAll(currentTransactionsList);
            }
            return 0;
        }

        int minTransactions = Integer.MAX_VALUE;

        for (int i = start + 1; i < balances.size(); i++) {
            if (balances.get(start) * balances.get(i) < 0) { // Different signs (creditor vs debtor)
                // Determine who pays whom
                int amount = Math.min(Math.abs(balances.get(start)), Math.abs(balances.get(i)));
                String transaction = "Person " + persons.get(start) +
                                     " pays Person " + persons.get(i) +
                                     " amount " + amount;
                currentTransactionsList.add(transaction);

                // Settle part or full balance
                balances.set(i, balances.get(i) + balances.get(start));

                // Recurse to find minimal transactions
                minTransactions = Math.min(minTransactions, 1 + settleDebts(balances, persons, start + 1, 
                                                                           currentTransactionsList, finalTransactionsList));

                // Undo changes (backtrack) to try other options
                balances.set(i, balances.get(i) - balances.get(start));

                // Remove transaction from list for backtracking
                currentTransactionsList.remove(currentTransactionsList.size() - 1);

                // Optimization: If exact match found, break to minimize further
                if (balances.get(i) == 0) {
                    break;
                }
            }
        }
        return minTransactions;
    }

    public static void main(String[] args) {
        SplitwiseSolution solution = new SplitwiseSolution();

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
