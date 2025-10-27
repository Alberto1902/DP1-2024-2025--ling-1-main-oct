export default function handleListPlayers(playersGame) {
    if(playersGame === undefined) {
        return "";
    }
    let playersList = "";
    playersGame.forEach((player) => {
        playersList += player.username + " ";
    });
    return playersList;
}