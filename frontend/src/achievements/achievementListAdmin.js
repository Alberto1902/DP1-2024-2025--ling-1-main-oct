import {
    Button, Table
} from "reactstrap";
import { useState, useEffect } from "react";
import tokenService from "../services/token.service";
import useFetchState from "../util/useFetchState";
import deleteFromList from "./../util/deleteFromList";
import getErrorModal from "./../util/getErrorModal";
import { Link } from "react-router-dom";
import '../static/css/achievements/achievementList.css';

const imgnotfound = "https://cdn-icons-png.flaticon.com/512/5778/5778223.png";
const jwt = tokenService.getLocalAccessToken();

export default function AchievementListAdmin() {
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const [alerts, setAlerts] = useState([]);
    const [achievements, setAchievements] = useState([]);
    const [totalPages, setTotalPages] = useState(0);
    const [page, setPage] = useState(0);
    const [size, setSize] = useState(10);
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
        } catch (error) { }
    };

    const handlePageChange = (newPage) => {
        setPage(newPage);
    };

    const handleSizeChange = (event) => {
        setSize(event.target.value);
    };

    useEffect(() => {
        fetchAchievements(page, size);
    }, [page, size]);

    const achievementList = achievements.map((a) => {
        return (
            <tr key={a.id}>
                <td className="text-center">{a.name}</td>
                <td className="text-center">{a.description}</td>
                <td className="text-center">{a.threshold}</td>
                <td className="text-center">{a.metric}</td>
                <td className="text-center">
                    <img
                        src={a.profilePictureUri ? a.profilePictureUri : imgnotfound}
                        alt={a.name}
                        width="50px"
                    />
                </td>
                <td className="text-center">
                    <Button
                        outline
                        color="warning"
                        style={{ width: '10vh', height: '6.5vh', display: 'flex', alignItems: 'center', justifyContent: 'center' }}
                    >
                        <Link
                            to={`/achievements/` + a.id}
                            className="btn sm"
                            style={{ textDecoration: 'none', alignItems: 'center' }}
                        >
                            Edit
                        </Link>
                    </Button>
                </td>
                <td className="text-center">
                    <Button
                        outline
                        color="danger"
                        style={{ width: '10vh', height: '6.5vh', display: 'flex', alignItems: 'center', justifyContent: 'center' }}
                        onClick={() =>
                            deleteFromList(
                                `/api/v1/achievements/${a.id}`,
                                a.id,
                                [achievements, setAchievements],
                                [alerts, setAlerts],
                                setMessage,
                                setVisible
                            )
                        }
                    >
                        Delete
                    </Button>
                </td>
            </tr>
        );
    });

    const modal = getErrorModal(setVisible, visible, message);

    return (
        <div className="home-page-container">
            <div className="admin-page-container">
                {alerts.map((a) => a.alert)}
                {modal}
                <div className='table-container' style={{ width: '150vh', color: 'white', marginTop: '10vh' }}>
                    <h1 className="text-center">Achievements</h1>
                    <Table aria-label="achievements" className="mt-4">
                        <thead>
                            <tr>
                                <th className="text-center">Name</th>
                                <th className="text-center">Description</th>
                                <th className="text-center">Threshold</th>
                                <th className="text-center">Metric</th>
                                <th className="text-center">Reward</th>
                                <th className="text-center">Actions</th>
                                <th className="text-center"></th>
                            </tr>
                        </thead>
                        <tbody>{achievementList}</tbody>
                    </Table>                        <div>
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
                <div style={{ backgroundColor: 'white', borderRadius: '1vh', position: 'absolute', right: '30vh', top: '85vh', marginTop: '1vh' }}>
                    <Button outline color="success">
                        <Link
                            to={`/achievements/new`}
                            className="btn sm"
                            style={{ textDecoration: "none", backgroundColor: 'transparent' }}
                        >
                            Create achievement
                        </Link>
                    </Button>
                </div>
            </div>
        </div>
    );
}
