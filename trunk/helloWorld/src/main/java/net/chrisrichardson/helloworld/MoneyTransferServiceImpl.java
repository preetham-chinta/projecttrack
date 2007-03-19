/*
 * Copyright (c) 2005 Chris Richardson
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.chrisrichardson.helloworld;

public class MoneyTransferServiceImpl implements MoneyTransferService {

	private final AccountRepository accountRepository;

	public MoneyTransferServiceImpl(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	public void transfer(int fromAccountId, int toAccountId, double amount) {
		Account fromAccount = accountRepository.findAccount(fromAccountId);
		Account toAccount = accountRepository.findAccount(toAccountId);
		fromAccount.debit(amount);
		toAccount.credit(amount);
	}

}
