import {render, screen, fireEvent} from "../test-utils";
import userEvent from "@testing-library/user-event";
import GameSessionsListAdminFinished from "./gameSessionListAdminFinished";
import useFetchState from "../util/useFetchState";
jest.mock("../util/useFetchState");

const mockGameSessions = [
    {
        id: 1,
        name: "Game 1",
        currentPlayers: 3,
        maxPlayers: 5,
        isPrivate: false,
        creator: { username: "admin" },
        players: [
            { username: "player1" },
            { username: "player2" },
            { username: "player3" }
        ],
        winner: { username: "player2" }
    },
    {
        id: 2,
        name: "Game 2",
        currentPlayers: 4,
        maxPlayers: 4,
        isPrivate: true,
        creator: { username: "creator" },
        players: [
            { username: "user1" },
            { username: "user2" },
            { username: "user3" },
            { username: "user4" }
        ],
        winner: { username: "user4" }
    }
];

describe("GameSessionsListAdminFinished", () => {
    beforeEach(() => {
        useFetchState.mockReturnValue([mockGameSessions, jest.fn()]);
    });

    test("renders game session data correctly", () => {
        render(<GameSessionsListAdminFinished />);

        expect(screen.getByText("Game 1")).toBeInTheDocument();
        expect(screen.getByText("3/5")).toBeInTheDocument();
        expect(screen.getByText("No")).toBeInTheDocument();
        expect(screen.getByText("admin")).toBeInTheDocument();
        expect(screen.getByText(/player1 player2 player3/i)).toBeInTheDocument();
        expect(screen.getByText("player2")).toBeInTheDocument();
    });

    test("renders pagination controls correctly", () => {
        render(<GameSessionsListAdminFinished />);

        expect(screen.getByText(/Page 1 of 1/i)).toBeInTheDocument();
        expect(screen.getByText(/Previous/i)).toBeDisabled();
        expect(screen.getByText(/Next/i)).toBeDisabled();
    });

    test("handles pagination buttons correctly", () => {
        const largerMockData = Array.from({ length: 10 }, (_, index) => ({
            id: index + 1,
            name: `Game ${index + 1}`,
            currentPlayers: 2,
            maxPlayers: 4,
            isPrivate: false,
            creator: { username: `creator${index + 1}` },
            players: [],
            winner: { username: "winner" }
        }));

        useFetchState.mockReturnValue([largerMockData, jest.fn()]);

        render(<GameSessionsListAdminFinished />);

        const nextButton = screen.getByText(/Next/i);

        fireEvent.click(nextButton);

        expect(screen.getByText(/Page 2 of 2/i)).toBeInTheDocument();
        fireEvent.click(screen.getByText(/Previous/i));
        expect(screen.getByText(/Page 1 of 2/i)).toBeInTheDocument();
    });
});