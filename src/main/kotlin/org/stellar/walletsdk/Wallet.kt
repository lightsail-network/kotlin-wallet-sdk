package org.stellar.walletsdk

import org.stellar.sdk.*
import org.stellar.walletsdk.utils.buildTransaction
import org.stellar.walletsdk.utils.sponsorOperation

class Wallet(
  private val horizonUrl: String = "https://horizon-testnet.stellar.org",
  private val networkPassphrase: String = Network.TESTNET.toString()
) {
  private val server = Server(this.horizonUrl)
  private val network = Network(this.networkPassphrase)

  data class AccountKeypair(val publicKey: String, val secretKey: String)

  // Create account keys, generate new keypair
  fun create(): AccountKeypair {
    val keypair: KeyPair = KeyPair.random()
    return AccountKeypair(keypair.accountId, String(keypair.secretSeed))
  }

  // Fund (activate) account
  // TODO: ??? do we need to include add trustline operation here?
  fun fund(
    sourceAddress: String,
    destinationAddress: String,
    startingBalance: String = "1",
    sponsorAddress: String = ""
  ): Transaction {
    val isSponsored = sponsorAddress.isNotBlank()

    if (!isSponsored && startingBalance.toInt() < 1) {
      throw Error("Starting balance must be at least 1 XLM for non-sponsored accounts")
    }

    val startBalance = if (isSponsored) "0" else startingBalance

    val createAccountOp: CreateAccountOperation =
      CreateAccountOperation.Builder(destinationAddress, startBalance)
        .setSourceAccount(sourceAddress)
        .build()

    val operations: List<Operation> =
      if (isSponsored) {
        sponsorOperation(sponsorAddress, destinationAddress, createAccountOp)
      } else {
        listOfNotNull(createAccountOp)
      }

    return buildTransaction(sourceAddress, server, network, operations)
  }
}