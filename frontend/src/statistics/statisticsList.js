import { Button, Table } from "reactstrap"
import { useState, useEffect, useRef } from "react"
import tokenService from "../services/token.service"
import useFetchState from "../util/useFetchState"
import getErrorModal from "../util/getErrorModal"
import * as d3 from "d3"
import "../static/css/statistics/statistics.css"

const jwt = tokenService.getLocalAccessToken()

export default function RefactoredStatisticsList() {
    const [message, setMessage] = useState(null)
    const [visible, setVisible] = useState(false)
    const username = tokenService.getUser().username
    const userId = tokenService.getUser().id
    const [currentUser, setUser] = useFetchState([], `/api/v1/users/${username}`, jwt)
    const [statistics, setStatistics] = useFetchState([], `/api/v1/statistics/user?userId=${userId}`, jwt, setMessage, setVisible)
    const [globalStatistics, setGlobalStatistics] = useFetchState([], `/api/v1/statistics/global`, jwt, setMessage, setVisible)
    const [totalUsers, setTotalUsers] = useFetchState([], `/api/v1/users/countPlayers`, jwt, setMessage, setVisible)
    const [totalOnlineUsers, setTotalOnlineUsers] = useFetchState([], `/api/v1/users/countOnlinePlayers`, jwt, setMessage, setVisible)
    const [mostFoughtSquare, setMostFoughtSquare] = useFetchState([], `/api/v1/fights/mostFoughtSquare`, jwt, setMessage, setVisible)
    const [viewMode, setViewMode] = useState("table")
    const barChartRef = useRef(null)
    const victoryChartRef = useRef(null)
    const achievementChartRef = useRef(null)
    const [achievementProgress, setAchievementProgress] = useState(0)


    const images = {
        gamesPlayed: "http://www.pngmart.com/files/4/Island-PNG-Clipart.png",
        victories: "https://pngimg.com/uploads/treasure_chest/treasure_chest_PNG159.png",
        defeats: "https://i.pinimg.com/originals/ac/8f/a6/ac8fa6e5977b2561eda998f80f5fa655.png",
        winRatio: "https://freepngimg.com/thumb/stock_market/26072-3-stock-market-file.png",
        lossRatio: "https://cdn3d.iconscout.com/3d/premium/thumb/loss-arrow-5080255-4249096.png"
    }

    useEffect(() => {
        if (viewMode === "chart" && statistics.gamesPlayed > 0) {
            drawBarChart()
        }
    }, [viewMode, statistics])

    useEffect(() => {
        if (viewMode === "table" && statistics.gamesPlayed > 0) {
            const victoryData = [
                { label: "Victories", value: statistics.victories },
                { label: "Defeats", value: statistics.defeats },
            ]
            drawD3Chart(victoryChartRef, victoryData, ["green", "darkred"])
        }
    }, [viewMode, statistics])

    useEffect(() => {
        if (viewMode === "table") {
            const achievementData = [
                { label: "Progress", value: achievementProgress },
                { label: "Remaining", value: 100 - achievementProgress },
            ]
            drawD3Chart(achievementChartRef, achievementData, ["#4caf50", "#ddd"])
        }
    }, [viewMode, achievementProgress])


    useEffect(() => {
        fetchUserStats()
        fetchGlobalStats()
    }, [])

    useEffect(() => {
        if (currentUser.obtainedAchievements) {
            fetch("/api/v1/achievements", {
                headers: { Authorization: `Bearer ${jwt}` }
            })
                .then(response => response.json())
                .then(totalAchievements => {
                    console.log("totalAchievements", totalAchievements)
                    const obtainedCount = currentUser.obtainedAchievements.length
                    const total = totalAchievements.content.length
                    const progressNum = total > 0 ? (obtainedCount / total) * 100 : 0
                    setAchievementProgress(progressNum)
                })
                .catch(() => setMessage("Error fetching total achievements"))
        }
    }, [currentUser, jwt])

    const fetchUserStats = () => {
        const method = Object.keys(statistics).length === 0 ? "POST" : "PUT"
        fetch(`/api/v1/statistics/user?userId=${userId}`, {
            method,
            headers: { Authorization: `Bearer ${jwt}` }
        })
            .then(response => response.json())
            .then(data => setStatistics(data))
            .catch(() => setMessage("Error fetching statistics"))
    }

    const fetchGlobalStats = () => {
        const method = "PUT"
        fetch(`/api/v1/statistics/global`, {
            method,
            headers: { Authorization: `Bearer ${jwt}` }
        })
            .then(response => response.json())
            .then(data => setGlobalStatistics(data))
            .catch(() => setMessage("Error fetching statistics"))
    }

    const drawBarChart = () => {
        const data = [
            { label: "Games Played", value: statistics.gamesPlayed },
            { label: "Victories", value: statistics.victories },
            { label: "Defeats", value: statistics.defeats },
            { label: "Win Ratio (%)", value: statistics.winRatio },
            { label: "Loss Ratio (%)", value: statistics.lossRatio }
        ]
        const margin = { top: 50, right: 50, bottom: 100, left: 100 }
        const width = 800 - margin.left - margin.right
        const height = 600 - margin.top - margin.bottom
        const container = d3.select(barChartRef.current)

        container.selectAll("*").remove()

        const svg = container
            .attr("width", width + margin.left + margin.right)
            .attr("height", height + margin.top + margin.bottom)
            .append("g")
            .attr("transform", `translate(${margin.left},${margin.top})`)

        const x = d3.scaleBand()
            .domain(data.map(d => d.label))
            .range([0, width])
            .padding(0.1)

        const y = d3.scaleLinear()
            .domain([0, d3.max(data, d => d.value)])
            .nice()
            .range([height, 0])

        const xAxis = d3.axisBottom(x)
        const yAxis = d3.axisLeft(y)

        svg.append("g")
            .attr("transform", `translate(0,${height})`)
            .call(xAxis)
            .selectAll("text")
            .attr("transform", "rotate(-45)")
            .style("text-anchor", "end")
            .style("font-size", "14px")

        svg.append("g")
            .call(yAxis)
            .selectAll("text")
            .style("font-size", "14px")

        svg.selectAll(".bar")
            .data(data)
            .enter()
            .append("rect")
            .attr("class", "bar")
            .attr("x", d => x(d.label))
            .attr("y", d => y(d.value))
            .attr("width", x.bandwidth())
            .attr("height", d => height - y(d.value))
            .attr("fill", "steelblue")

        svg.selectAll(".bar-label")
            .data(data)
            .enter()
            .append("text")
            .attr("class", "bar-label")
            .attr("x", d => x(d.label) + x.bandwidth() / 2)
            .attr("y", d => y(d.value) - 5)
            .attr("text-anchor", "middle")
            .style("font-size", "14px")
            .style("fill", "black")
            .text(d => Number(d.value).toFixed(2))
    }

    const drawD3Chart = (ref, data, colors) => {
        const width = 150
        const height = 150
        const radius = Math.min(width, height) / 2
        const container = d3.select(ref.current)

        container.selectAll("*").remove()

        const svg = container
            .attr("width", width)
            .attr("height", height)
            .append("g")
            .attr("transform", `translate(${width / 2},${height / 2})`)

        const pie = d3.pie().value(d => d.value)
        const arc = d3.arc().innerRadius(30).outerRadius(radius)

        svg.selectAll("path")
            .data(pie(data))
            .enter()
            .append("path")
            .attr("d", arc)
            .attr("fill", (d, i) => colors[i])
    }

    return (
        <div className="statistics-container">
            <div className="header-section">
                <h1 className="statistics-title">Statistics</h1>
                <Button
                    color="primary"
                    onClick={() => setViewMode(viewMode === "table" ? "chart" : "table")}
                >
                    Switch to {viewMode === "table" ? "Chart" : "Table"} View
                </Button>
            </div>

            {viewMode === "table" ? (
                <div className="statistics-table-container">
                    {/* Columna izquierda: tablas */}
                    <div className="left-tables-container">
                        <div className="table-section">
                            <Table className="custom-table">
                                <tbody>
                                    <tr>
                                        <td>Games Played</td>
                                        <td>{statistics.gamesPlayed}</td>
                                    </tr>
                                    <tr>
                                        <td>Victories</td>
                                        <td>{statistics.victories}</td>
                                    </tr>
                                    <tr>
                                        <td>Defeats</td>
                                        <td>{statistics.defeats}</td>
                                    </tr>
                                    <tr>
                                        <td>Win Ratio (%)</td>
                                        <td>{Number(statistics.winRatio).toFixed(2)}</td>
                                    </tr>
                                    <tr>
                                        <td>Loss Ratio (%)</td>
                                        <td>{Number(statistics.lossRatio).toFixed(2)}</td>
                                    </tr>
                                    <tr>
                                        <td>Shortest Game</td>
                                        <td>{statistics.shortestGame}</td>
                                    </tr>
                                    <tr>
                                        <td>Longest Game</td>
                                        <td>{statistics.longestGame}</td>
                                    </tr>
                                    <tr>
                                        <td>Average Game</td>
                                        <td>
                                            {Number(statistics.averageGameDuration).toFixed(2)}
                                        </td>
                                    </tr>
                                    <tr>
                                        <td>Smallest Game Room</td>
                                        <td>{statistics.smallestGameRoom}</td>
                                    </tr>
                                    <tr>
                                        <td>Biggest Game Room Size</td>
                                        <td>{statistics.biggestGameRoomSize}</td>
                                    </tr>
                                    <tr>
                                        <td>Average Game Room Size</td>
                                        <td>
                                            {Number(statistics.averageGameRoomSize).toFixed(2)}
                                        </td>
                                    </tr>
                                    <tr>
                                        <td>Global Games Played</td>
                                        <td>{globalStatistics.gamesPlayed}</td>

                                    </tr>
                                    <tr>
                                        <td>Global Shortest Game</td>
                                        <td>{globalStatistics.shortestGame}</td>
                                    </tr>
                                    <tr>
                                        <td>Global Longest Game</td>
                                        <td>{globalStatistics.longestGame}</td>
                                    </tr>
                                    <tr>
                                        <td>Global Average Game Duration</td>
                                        <td>
                                            {Number(globalStatistics.averageGameDuration).toFixed(2)}
                                        </td>
                                    </tr>
                                    <tr>
                                        <td>Global Average Game Room Size</td>
                                        <td>
                                            {Number(globalStatistics.averageGameRoomSize).toFixed(2)}
                                        </td>
                                    </tr>
                                    <tr>
                                        <td>Most Fought Square</td>
                                        <td>
                                            {mostFoughtSquare.name? mostFoughtSquare.name : "Nameless Square"}
                                        </td>
                                    </tr>
                                    <tr>
                                        <td>Total Registered Users</td>
                                        <td>{totalUsers}</td>
                                    </tr>
                                    <tr>
                                        <td>Online Users</td>
                                        <td>{totalOnlineUsers}</td>
                                    </tr>
                                </tbody>
                            </Table>
                        </div>
                    </div>
                    {/* Columna derecha: winrate arriba, achievements abajo */}
                    <div className="right-charts-container">
                        <div className="winrate-container">
                            <h4>Winrate</h4>
                            <svg ref={victoryChartRef} width={150} height={150}></svg>
                            <p className="winrate-text">
                                {Number(statistics.winRatio).toFixed(2)}% Victories
                            </p>
                        </div>

                        <div className="achievement-container">
                            <h4>Achievement Progress</h4>
                            <svg ref={achievementChartRef} width={150} height={150}></svg>
                            <p>{achievementProgress}% of achievements unlocked</p>
                        </div>
                    </div>
                </div>
            ) : (
                <div className="chart-container">
                    <h4>Your Statistics</h4>
                    <svg ref={barChartRef} width={500} height={300}></svg>
                </div>
            )}
        </div>
    )
}
