find . -type f -name "*" -exec sed -i'' -e 's/satoshiquest/cryptoplugin/g' {} +
find . -type f -name "*" -exec sed -i'' -e 's/SatoshiQuest/CryptoPlugin/g' {} +
find . -type f -name "*" -exec sed -i'' -e 's/Satoshiquest/Cryptoplugin/g' {} +
find . -type f -name "*" -exec sed -i'' -e 's/satoshiQuest/cryptoPlugin/g' {} +
find . -type f -name "*" -exec sed -i'' -e 's/SATOSHIQUEST/CRYPTOPLUGIN/g' {} +
shopt -s globstar
find . * | rename 's/satoshiquest/cryptoplugin/g'
shopt -s globstar
find . * | rename 's/satoshiquest/cryptoplugin/g'
shopt -s globstar
find . * | rename 's/SatoshiQuest/CryptoPlugin/g'
shopt -s globstar
find . * | rename 's/Satoshiquest/Cryptoplugin/g'
shopt -s globstar
find . * | rename 's/satoshiQuest/cryptoPlugin/g'
shopt -s globstar
find . * | rename 's/SATOSHIQUEST/CRYPTOPLUGIN/g'
