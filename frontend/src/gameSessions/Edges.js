import React from 'react';

const Edges = ({ edges, squares }) => {


    return (
        <svg className="edges-svg">
            {[...edges].map(edgeKey => {
                const [squareId, neighborId] = edgeKey.split('-').map(Number);
                const square = squares.find(s => s.id === squareId);
                const neighbor = squares.find(s => s.id === neighborId);

                if (!square || !neighbor) return null;
                return (
                    <line
                        key={edgeKey}
                        x1={`${square.xposition}%`}
                        y1={`${square.yposition}%`}
                        x2={`${neighbor.xposition}%`}
                        y2={`${neighbor.yposition}%`}
                        stroke="black"
                        strokeOpacity="0.0"
                        strokeWidth="2"
                    />
                );
            })}
        </svg>
    );
};

export default Edges;
