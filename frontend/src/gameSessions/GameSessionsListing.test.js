import {render, screen} from "../test-utils";
import userEvent from "@testing-library/user-event";
import GameSessionsList from "./GameSessionsList";

describe('test3GameSessionsList', () => {
    test('should render games correctly', async () => {
        render(<GameSessionsList />);
        const game1 = await screen.findByRole('cell', {name: 'Prueba1'});
        expect(game1).toBeInTheDocument();
    });
})