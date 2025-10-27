import {
    Table, Button
} from "reactstrap";

import tokenService from "../services/token.service";
import useFetchState from "../util/useFetchState";
import { Link } from "react-router-dom";
import { useContext, useState } from "react";

const jwt = tokenService.getLocalAccessToken();

export default function GameSessionsListAdminFinished() {
    const [gamesessions, setGamesessions] = useFetchState(
        [],
        `/api/v1/gamesessions?status=FINISHED`,
        jwt
    );

    function handleListPlayers(players) {
        let playersList = "";
        players.forEach((player) => {
            playersList += player.username + " ";
        });
        return playersList;
    }

    const [currentPage, setCurrentPage] = useState(1);
    const itemsPerPage = 5;

    const indexOfLastItem = currentPage * itemsPerPage;
    const indexOfFirstItem = indexOfLastItem - itemsPerPage;
    const currentGames = gamesessions.slice(indexOfFirstItem, indexOfLastItem);

    const totalPages = Math.ceil(gamesessions.length / itemsPerPage);

    const gamesessionsList = currentGames.map((a) => {
        return (
            <tr key={a.id}>
                <td className="text-center">{a.name}</td>
                <td className="text-center">{a.currentPlayers + "/" + a.maxPlayers}</td>
                <td className="text-center">{a.isPrivate ? 'Yes' : 'No'}</td>
                <td className="text-center">{a.creator.username}</td>
                <td className="text-center">{handleListPlayers(a.players)}</td>
                <td className="text-center">{a.winner.username}</td>
            </tr>
        );
    });

    const emptyRows = Array.from(
        { length: itemsPerPage - currentGames.length },
        (_, index) => (
            <tr key={`empty-${index}`}>
                <td className="text-center">-</td>
                <td className="text-center">-</td>
                <td className="text-center">-</td>
                <td className="text-center">-</td>
                <td className="text-center">-</td>
                <td className="text-center">-</td>
            </tr>
        )
    );

    const handleNextPage = () => {
        if (currentPage < totalPages) setCurrentPage(currentPage + 1);
    };

    const handlePreviousPage = () => {
        if (currentPage > 1) setCurrentPage(currentPage - 1);
    };

    return (
        <div>
            <div className="admin-page-containe" style={{
                display: "flex",
                flexDirection: "column",
                alignItems: "center",
                justifyContent: "center",
                backgroundColor: "#f8f9fa",
                borderRadius: "15px",
                boxShadow: "0px 4px 8px rgba(0, 0, 0, 0.1)",
                maxWidth: "80%",
                margin: "6rem auto",
                padding: "2rem"
            }}>
                <h1 className="text-center" style={{
                    textAlign: "center",
                    marginBottom: "15px",
                    fontSize: "1.5rem",
                    color: "#333"
                }}>Game Sessions</h1>
                <div className="table-responsive" style={{
                    width: "100%",
                    overflowX: "auto"
                }}>
                    <Table aria-label="gamesessions" className="table-custom" style={{
                        width: "100%",
                        marginBottom: "15px",
                        borderCollapse: "separate",
                        borderSpacing: "0 10px"
                    }}>
                        <thead>
                            <tr>
                                <th className="text-center">Game Name</th>
                                <th className="text-center">Number of Players</th>
                                <th className="text-center">Private</th>
                                <th className="text-center">Creator</th>
                                <th className="text-center">Players</th>
                                <th className="text-center">Winner</th>
                            </tr>
                        </thead>
                        <tbody>
                            {gamesessionsList}
                            {emptyRows}
                        </tbody>
                    </Table>
                </div>
                <div className="pagination-controls text-center" style={{
                    marginTop: "10px",
                    display: "flex",
                    justifyContent: "center",
                    gap: "10px"
                }}>
                    <Button onClick={handlePreviousPage} disabled={currentPage === 1} color="primary">
                        Previous
                    </Button>
                    <span className="page-info" style={{
                        margin: "0 10px",
                        fontSize: "0.9rem",
                        color: "#555"
                    }}>Page {currentPage} of {totalPages}</span>
                    <Button onClick={handleNextPage} disabled={currentPage === totalPages} color="primary">
                        Next
                    </Button>
                </div>
            </div>
        </div>
    );
}