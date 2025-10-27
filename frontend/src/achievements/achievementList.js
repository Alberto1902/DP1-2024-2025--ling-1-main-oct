import {
    Button,
    Table,
    Progress,
    Modal,
    ModalHeader
} from "reactstrap";
import { useState, useEffect } from "react";
import tokenService from "../services/token.service";
import useFetchState from "../util/useFetchState";
import getErrorModal from "./../util/getErrorModal";
import lockedHover from "./../static/images/bloqueado hover.png";
import lockedNoHover from "./../static/images/bloqueado no hover.png";
import unlocked from "./../static/images/desbloqueado.png";
import '../static/css/achievements/achievementList.css';

const imgnotfound = "https://cdn-icons-png.flaticon.com/512/5778/5778223.png";
const jwt = tokenService.getLocalAccessToken();

export default function AchievementList() {
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const [hoveredAchievement, setHoveredAchievement] = useState(null);
    const [modalData, setModalData] = useState(null);
    const [isModalOpen, setModalOpen] = useState(false);
    const username = tokenService.getUser().username;
    const userId = tokenService.getUser().id;
    const [currentUser, setUser] = useFetchState(
        [],
        `/api/v1/users/${username}`,
        jwt
    );
    const [achievements, setAchievements] = useState([]);
    const [page, setPage] = useState(0);
    const [size, setSize] = useState(10);
    const [totalPages, setTotalPages] = useState(0);
    const [gamesPlayed, setGamesPlayed] = useFetchState(
        [],
        `/api/v1/gamesessions/totalGames?userId=${userId}`,
        jwt,
        setMessage,
        setVisible
    );
    const [victories, setVictories] = useFetchState(
        [],
        `/api/v1/gamesessions/totalWins?userId=${userId}`,
        jwt,
        setMessage,
        setVisible
    );
    const [timePlayed, setTimePlayed] = useFetchState(
        [],
        `/api/v1/gamesessions/minutesPlayedUser?userId=${userId}`,
        jwt,
        setMessage,
        setVisible
    );

    useEffect(() => {
        fetchAchievements(page, size);
    }, [page, size]);

    const fetchAchievements = async (page, size) => {
        try {
            const response = await fetch(`/api/v1/achievements?page=${page}&size=${size}`, {
                headers: {
                    Authorization: `Bearer ${jwt}`
                }
            });
            const data = await response.json();
            setAchievements(data.content || []);
            setTotalPages(data.totalPages || 0);
        } catch (error) {}
    };

    const handlePageChange = (newPage) => {
        setPage(newPage);
    };

    const handleSizeChange = (event) => {
        setSize(event.target.value);
    };

    const handleStatus = (a) => {
        if (currentUser.obtainedAchievements?.some((ach) => ach.id === a.id)) {
            return unlocked;
        }
        return a.id === hoveredAchievement ? lockedHover : lockedNoHover;
    };

    const handleProgress = (a) => {
        switch (a.metric) {
            case "GAMES_PLAYED":
                return Math.min((gamesPlayed / a.threshold) * 100, 100);
            case "VICTORIES":
                return Math.min((victories / a.threshold) * 100, 100);
            case "DEFEATS":
                return Math.min(((gamesPlayed - victories) / a.threshold) * 100, 100);
            case "TIME_PLAYED":
                return Math.min((timePlayed / a.threshold) * 100, 100);
            default:
                return 0;
        }
    };

    const handleModalOpen = (achievement) => {
        setModalData(achievement);
        setModalOpen(true);
    };

    const handleModalClose = () => {
        setModalOpen(false);
        setModalData(null);
    };

    const handleValidation = (a) => {
        if (a.metric === "GAMES_PLAYED" && gamesPlayed < a.threshold) {
            setMessage("You have not played enough games to claim this achievement!");
            setVisible(true);
            return false;
        } else if (a.metric === "VICTORIES" && victories < a.threshold) {
            setMessage("You have not won enough games to claim this achievement!");
            setVisible(true);
            return false;
        } else if (a.metric === "DEFEATS" && (gamesPlayed - victories) < a.threshold) {
            setMessage("You have not lost enough games to claim this achievement!");
            setVisible(true);
            return false;
        } else if (a.metric === "TIME_PLAYED" && timePlayed < a.threshold) {
            setMessage("You have not played enough time to claim this achievement!");
            setVisible(true);
            return false;
        } else if (
            a.metric !== "GAMES_PLAYED" &&
            a.metric !== "VICTORIES" &&
            a.metric !== "DEFEATS" &&
            a.metric !== "TIME_PLAYED"
        ) {
            setMessage("This logic is yet to be implemented!");
            setVisible(true);
            return false;
        }
        return true;
    };

    const handleClaiming = (a) => {
        fetch("/api/v1/users/claimAchievement", {
            method: 'PUT',
            headers: {
                Authorization: `Bearer ${jwt}`,
                Accept: 'application/json',
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(a),
        })
            .then((response) => response.text())
            .then((data) => {
                try {
                    const json = JSON.parse(data);
                    if (json.message) {
                        setMessage(json.message);
                        setVisible(true);
                    }
                } catch (e) {}
                window.location.reload();
            });
    };

    const achievementList = achievements.map((a) => {
        const progress = handleProgress(a);
        return (
            <tr
                key={a.id}
                onMouseEnter={() => setHoveredAchievement(a.id)}
                onMouseLeave={() => setHoveredAchievement(null)}
            >
                <td className="text-center" onClick={() => handleModalOpen(a)}>{a.name}</td>
                <td className="text-center">{a.description}</td>
                <td className="text-center">
                    <img
                        src={a.badgeImage ? a.badgeImage : imgnotfound}
                        alt={a.name}
                        width="50px"
                    />
                </td>
                <td className="text-center">
                    <Progress
                        value={progress}
                        color={progress === 100 ? "primary" : "info"}
                        style={{
                            height: "20px",
                            borderRadius: "10px"
                        }}
                    >
                        {Math.round(progress)}%
                    </Progress>
                </td>
                <td className="text-center">
                    <Button
                        onClick={() => {
                            const valid = handleValidation(a);
                            if (valid) {
                                handleClaiming(a);
                            }
                        }}
                        disabled={
                            progress < 100 ||
                            currentUser.obtainedAchievements?.some((ach) => ach.id === a.id)
                        }
                        style={{ border: "none", outline: "none", boxShadow: "none", padding: 0 }}
                    >
                        <img src={handleStatus(a)} alt="status" width="120px" />
                    </Button>
                </td>
            </tr>
        );
    });

    const modal = getErrorModal(setVisible, visible, message);

    return (
        <div>
            <div className="home-page-container">
                <div className="admin-page-container">
                    {modal}
                    <div className="table-container" style={{ color: "white", width: "150vh", marginTop: "10vh" }}>
                        <h1 className="text-center">Achievements</h1>
                        <Table aria-label="achievements" className="mt-4">
                            <thead>
                                <tr>
                                    <th className="text-center">Name</th>
                                    <th className="text-center">Description</th>
                                    <th className="text-center">Image</th>
                                    <th className="text-center">Progress</th>
                                    <th className="text-center"></th>
                                </tr>
                            </thead>
                            <tbody>{achievementList}</tbody>
                        </Table>
                        <div>
                            <label>
                                Page Size:
                                <select value={size} onChange={handleSizeChange}>
                                    <option value={5}>5</option>
                                    <option value={10}>10</option>
                                    <option value={20}>20</option>
                                </select>
                            </label>
                        </div>
                        <div>
                            {Array.from({ length: totalPages }, (_, index) => (
                                <button
                                    key={index}
                                    onClick={() => handlePageChange(index)}
                                    disabled={index === page}
                                >
                                    {index + 1}
                                </button>
                            ))}
                        </div>
                    </div>
                </div>
            </div>
            <Modal isOpen={isModalOpen} toggle={handleModalClose} centered>
                <ModalHeader toggle={handleModalClose}>{modalData?.name}</ModalHeader>
            </Modal>
        </div>
    );
}
