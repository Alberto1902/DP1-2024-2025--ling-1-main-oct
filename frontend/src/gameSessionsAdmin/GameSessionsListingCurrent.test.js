import { render, screen, fireEvent } from "@testing-library/react";
import GameSessionsListAdminCurrent from "./gameSessionListAdminCurrent";
import tokenService from "../services/token.service";
import useFetchState from "../util/useFetchState";

jest.mock("../services/token.service");
jest.mock("../util/useFetchState");

describe("GameSessionsListAdminCurrent Component", () => {
    beforeEach(() => {
        tokenService.getLocalAccessToken.mockReturnValue("mocked-token");
        tokenService.getUser.mockReturnValue({ username: "admin" });

        useFetchState.mockReturnValue([
            [
                { id: 1, name: "Prueba1", currentPlayers: 2, maxPlayers: 4, isPrivate: true, creator: { username: "creator1" }, players: ["player1", "player2"] },
                { id: 2, name: "Prueba2", currentPlayers: 3, maxPlayers: 5, isPrivate: false, creator: { username: "creator2" }, players: ["player3"] },
                { id: 3, name: "Prueba3", currentPlayers: 1, maxPlayers: 4, isPrivate: false, creator: { username: "creator3" }, players: [] },
                { id: 4, name: "Prueba4", currentPlayers: 4, maxPlayers: 4, isPrivate: false, creator: { username: "creator4" }, players: ["player1", "player2", "player3", "player4"] },
                { id: 5, name: "Prueba5", currentPlayers: 3, maxPlayers: 4, isPrivate: false, creator: { username: "creator5" }, players: ["player5", "player6", "player7"] },
                { id: 6, name: "Prueba6", currentPlayers: 3, maxPlayers: 5, isPrivate: false, creator: { username: "creator6" }, players: ["player7", "player8", "player9"] },
                { id: 7, name: "Prueba7", currentPlayers: 3, maxPlayers: 5, isPrivate: false, creator: { username: "creator6" }, players: ["player7", "player8", "player9"] },
                { id: 8, name: "Prueba8", currentPlayers: 3, maxPlayers: 5, isPrivate: false, creator: { username: "creator6" }, players: ["player7", "player8", "player9"] },
                { id: 9, name: "Prueba9", currentPlayers: 3, maxPlayers: 5, isPrivate: false, creator: { username: "creator6" }, players: ["player7", "player8", "player9"] },
                { id: 10, name: "Prueba10", currentPlayers: 3, maxPlayers: 5, isPrivate: false, creator: { username: "creator6" }, players: ["player7", "player8", "player9"] },
                { id: 11, name: "Prueba11", currentPlayers: 3, maxPlayers: 5, isPrivate: false, creator: { username: "creator6" }, players: ["player7", "player8", "player9"] },
            ],
            jest.fn(),
        ]);
    });

    it("renders the component correctly", () => {
        render(<GameSessionsListAdminCurrent />);

        expect(screen.getByText("Game Sessions")).toBeInTheDocument();
        expect(screen.getByText("Game Name")).toBeInTheDocument();
        expect(screen.getByText("Number of Players")).toBeInTheDocument();
        expect(screen.getByText("Private")).toBeInTheDocument();
        expect(screen.getByText("Creator")).toBeInTheDocument();
        expect(screen.getByText("Players")).toBeInTheDocument();
    });

    it("displays game session data", () => {
        render(<GameSessionsListAdminCurrent />);
        const gameNames = [
            "Prueba1", "Prueba2", "Prueba3", "Prueba4", "Prueba5"
        ];
        gameNames.forEach((name) => {
            expect(
                screen.getByText((content, element) => content.includes(name))
            ).toBeInTheDocument();
        });
    
        const noElements = screen.getAllByText("No");
        expect(noElements.length).toBeGreaterThan(0);
    
        const yesElements = screen.getAllByText("Yes");
        expect(yesElements.length).toBeGreaterThan(0);
    });
    

    it("handles pagination correctly", () => {
        render(<GameSessionsListAdminCurrent />);

        const nextButton = screen.getByText("Next");
        const previousButton = screen.getByText("Previous");

        expect(nextButton).toBeEnabled();
        expect(previousButton).toBeDisabled();

        fireEvent.click(nextButton);
        expect(previousButton).toBeEnabled();
        expect(nextButton).toBeEnabled();

        fireEvent.click(nextButton);
        expect(previousButton).toBeEnabled();
        expect(nextButton).toBeDisabled();
    });
});
