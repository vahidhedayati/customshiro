// Shiro config
beans {
	credentialMatcher(org.apache.shiro.authc.credential.HashedCredentialsMatcher) {
		hashAlgorithmName=org.apache.shiro.crypto.hash.Sha512Hash.ALGORITHM_NAME
		hashIterations=1024
        storedCredentialsHexEncoded=false
    }
}
