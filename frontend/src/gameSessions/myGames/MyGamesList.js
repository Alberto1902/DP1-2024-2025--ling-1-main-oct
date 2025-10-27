import { Table, Button } from 'reactstrap'; 
import tokenService from '../../services/token.service';
import useFetchState from '../../util/useFetchState';
import { useContext, useState } from 'react';
import '../../static/css/game/gameList.css';

const jwt = tokenService.getLocalAccessToken();

export default function MyGamesList() {
  const user = tokenService.getUser();

  const [gamesessions, setGamesessions] = useFetchState(
    [],
    `/api/v1/gamesessions?creatorId=${user.id}&status=FINISHED`,
    jwt
  );

  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 5;

  const indexOfLastItem = currentPage * itemsPerPage;
  const indexOfFirstItem = indexOfLastItem - itemsPerPage;
  const currentGames = gamesessions.slice(indexOfFirstItem, indexOfLastItem);

  const totalPages = Math.ceil(gamesessions.length / itemsPerPage);

  const mygamessessions = currentGames.map((session) => {
    const players = session.players.map((player) => player.username).join(', ');
    return (
      <tr key={session.id}>
        <td className="text-center">{session.name}</td>
        <td className="text-center">{players}</td>
        <td className="text-center">{session.isPrivate ? 'Private' : 'Public'}</td>
        <td className="text-center">{session.winner ? session.winner.username : 'null'}</td>
      </tr>
    );
  });

  const handleNextPage = () => {
    if (currentPage < totalPages) setCurrentPage(currentPage + 1);
  };

  const handlePreviousPage = () => {
    if (currentPage > 1) setCurrentPage(currentPage - 1);
  };


  
  const emptyRows = Array.from(
    { length: itemsPerPage - currentGames.length },
    (_, index) => (
      <tr key={`empty-${index}`}>
        <td className="text-center"></td>
        <td className="text-center"></td>
        <td className="text-center"></td>
        <td className="text-center">‎</td>
      </tr>
    )
  );

  
  return (
    <div className="admin-page-containe">
      <h1 className="table-title">My Games</h1>
      <div className="table-responsive">
        <Table aria-label="gamesessions" className="table-custom" hover striped>
          <thead>
            <tr>
              <th className="text-center">Game</th>
              <th className="text-center">Players</th>
              <th className="text-center">Privacity</th>
              <th className="text-center">Winner</th>
            </tr>
          </thead>
          <tbody>
            {mygamessessions}
            {emptyRows}
          </tbody>
        </Table>
      </div>
      <div className="pagination-controls">
        <Button onClick={handlePreviousPage} disabled={currentPage === 1} color="primary">
          Previous
        </Button>
        <span className="page-info">Page {currentPage} of {totalPages}</span>
        <Button onClick={handleNextPage} disabled={currentPage === totalPages} color="primary">
          Next
        </Button>
      </div>
    </div>
  );
  
  
}