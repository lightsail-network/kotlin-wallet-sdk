package org.stellar.walletsdk

import org.stellar.sdk.KeyPair
import org.stellar.sdk.Network
import org.stellar.sdk.Server

class Wallet(private val horizonUrl: String, private val networkPassphrase: String) {
  private val server = Server(this.horizonUrl)
  private val network = Network(this.networkPassphrase)

  data class AccountKeypair(val publicKey: String, val secretKey: String)

  // Create account keys, generate new keypair
  fun create(): AccountKeypair {
    val keypair: KeyPair = KeyPair.random()
    return AccountKeypair(keypair.accountId, String(keypair.secretSeed))
  }
}
