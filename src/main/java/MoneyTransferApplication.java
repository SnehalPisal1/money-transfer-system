/*

3. Money Transfer Endpoint

Design a RESTful API endpoint to transfer money between two accounts.

Ensure atomicity and data consistency.

Handle edge cases (e.g., insufficient funds, invalid accounts).

Discuss how you would prevent race conditions in a concurrent environment.
 */

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ConcurrentHashMap;

@SpringBootApplication
@RestController
public class MoneyTransferApplication {
    ConcurrentHashMap<Long, Account> accounts = new ConcurrentHashMap<>();


    public MoneyTransferApplication() {
        accounts.put(1L, new Account(1L, 1000.0));
        accounts.put(2L, new Account(2L, 1000.0));
    }

    public static void main(String[] args) {
        SpringApplication.run(MoneyTransferApplication.class, args);
    }

    @PostMapping("/account/transfer")
    @Transactional
    public ResponseEntity<String> transferMoney(@RequestBody TransferRequest request) {
        double amount = request.getAmount();
        if(amount <=0 ){
            return ResponseEntity.badRequest().body("Amount must be positive");
        }
        if(request.getFromAccountId() == request.getToAccountId()){
            return ResponseEntity.badRequest().body("Accounts should be different");
        }

        Account fromAccount = accounts.get(request.getFromAccountId());
        Account toAccount = accounts.get(request.getToAccountId());

        if(fromAccount == null || toAccount == null){
            return ResponseEntity.badRequest().body("one or both accounts not found");
        }

        Account firstLock, secLock;
        if(fromAccount.getAccountId() < toAccount.getAccountId()){
            firstLock = fromAccount;
            secLock = toAccount;
        } else{
            firstLock = toAccount;
            secLock = fromAccount;
        }
    }

}