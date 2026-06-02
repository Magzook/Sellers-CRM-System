package ru.magzook.sellersrestservice.api_tests.helpers;

public class UrlConstructor {
    public static final String BASE = "/api/v1";

    public static final String SELLERS = BASE + "/sellers";
    public static final String TRANSACTIONS = BASE + "/transactions";
    public static final String ANALYTICS = BASE + "/analytics";

    public static final String TOP_1_SELLERS = ANALYTICS + "/top-1-sellers";
    public static final String SELLERS_BELOW_THRESHOLD = ANALYTICS + "/sellers-below-threshold";

    /**
     * /sellers/{id}
     */
    public static String sellersSlashId(int id) {
        return SELLERS + "/" + id;
    }

    /**
     * /sellers/{id}/transactions
     */
    public static String sellersSlashIdSlashTransactions(int sellerId) {
        return sellersSlashId(sellerId) + "/transactions";
    }

    /**
     * /transactions/{id}
     */
    public static String transactionsSlashId(long id) {
        return TRANSACTIONS + "/" + id;
    }
}
