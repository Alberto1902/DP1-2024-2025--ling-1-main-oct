import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { Button, ButtonGroup, Table } from "reactstrap";
import tokenService from "../../services/token.service";
import "../../static/css/admin/adminPage.css";
import deleteFromList from "../../util/deleteFromList";
import getErrorModal from "../../util/getErrorModal";
import useFetchState from "../../util/useFetchState";

const jwt = tokenService.getLocalAccessToken();

export default function UserListAdmin() {
  const [message, setMessage] = useState(null);
  const [visible, setVisible] = useState(false);
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [users, setUsers] = useState([]);
  const [totalPages, setTotalPages] = useState(0);
  const [alerts, setAlerts] = useState([]);

  useEffect(() => {
          fetchUsers(page, size);
      }, [page, size]);

  const fetchUsers = async (page, size) => {
    try {
        const response = await fetch(`/api/v1/users/pages?page=${page}&size=${size}`, {
            headers: {
                Authorization: `Bearer ${jwt}`
            }
        });
        const data = await response.json();
        setUsers(data.content || []);
        setTotalPages(data.totalPages || 0);
    } catch (error) {}
  };
  const handlePageChange = (newPage) => {
    setPage(newPage);
  };

  const handleSizeChange = (event) => {
    setSize(event.target.value);
  };

  const userList = users.map((user) => {
    return (
      <tr key={user.id}>
        <td>{user.username}</td>
        <td>{user.authority.authority}</td>
        <td>
          <ButtonGroup>
            <Button
              size="sm"
              aria-label={"edit-" + user.username}
              tag={Link}
              to={"/users/" + user.username}
              style={{
                backgroundColor: "#d2a679",
                border: "none",
                color: "#fff",
                fontWeight: "bold",
                padding: "5px 10px",
                borderRadius: "5px",
                transition: "all 0.3s ease-in-out",
              }}
              onMouseEnter={(e) =>
                (e.target.style.backgroundColor = "#b58968")
              }
              onMouseLeave={(e) =>
                (e.target.style.backgroundColor = "#d2a679")
              }
            >
              Edit
            </Button>
            <Button
              size="sm"
              aria-label={"delete-" + user.id}
              onClick={() =>
                deleteFromList(
                  `/api/v1/users/${user.id}`,
                  user.id,
                  [users, setUsers],
                  [alerts, setAlerts],
                  setMessage,
                  setVisible
                )
              }
              style={{
                backgroundColor: "#d2a679",
                border: "none",
                color: "#fff",
                fontWeight: "bold",
                padding: "5px 10px",
                borderRadius: "5px",
                transition: "all 0.3s ease-in-out",
                marginLeft: "5px",
              }}
              onMouseEnter={(e) =>
                (e.target.style.backgroundColor = "#b58968")
              }
              onMouseLeave={(e) =>
                (e.target.style.backgroundColor = "#d2a679")
              }
            >
              Delete
            </Button>
          </ButtonGroup>
        </td>
      </tr>

    );
  });
  const modal = getErrorModal(setVisible, visible, message);

  return (
    <div
      className="admin-page-container"
      style={{
        margin: "20px auto",
        padding: "20px",
        backgroundColor: "#fff",
        borderRadius: "10px",
        boxShadow: "0 4px 6px rgba(0, 0, 0, 0.1)",
        maxWidth: "800px",
      }}
    >
      <h1 className="text-center">Users</h1>
      {alerts.map((a) => a.alert)}
      {modal}
      <Button
        tag={Link}
        to="/users/new"
        style={{
          backgroundColor: "#d2a679",
          border: "none",
          color: "#fff",
          fontWeight: "bold",
          padding: "10px 20px",
          borderRadius: "5px",
          transition: "all 0.3s ease-in-out",
          textDecoration: "none",
        }}
        onMouseEnter={(e) =>
          (e.target.style.backgroundColor = "#b58968")
        }
        onMouseLeave={(e) =>
          (e.target.style.backgroundColor = "#d2a679")
        }
      >
        Add User
      </Button>

      <div>
        <Table aria-label="users" className="mt-4">
          <thead>
            <tr>
              <th>Username</th>
              <th>Authority</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>{userList}</tbody>
        </Table>
      </div>
      <div
        style={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          marginTop: "20px",
        }}
      >
        <label style={{ display: "flex", alignItems: "center" }}>
          Page Size:
          <select
            value={size}
            onChange={handleSizeChange}
            style={{
              marginLeft: "10px",
              padding: "5px 10px",
              border: "1px solid #ccc",
              borderRadius: "5px",
            }}
          >
            <option value={5}>5</option>
            <option value={10}>10</option>
            <option value={20}>20</option>
          </select>
        </label>
        <div style={{ display: "flex", gap: "5px" }}>
          {Array.from({ length: totalPages }, (_, index) => (
            <button
              key={index}
              onClick={() => handlePageChange(index)}
              disabled={index === page}
              style={{
                padding: "5px 10px",
                border: "1px solid #ccc",
                borderRadius: "5px",
                backgroundColor: index === page ? "#d2a679" : "#f0f0f0",
                color: index === page ? "white" : "#333",
                cursor: index === page ? "not-allowed" : "pointer",
                fontWeight: index === page ? "bold" : "normal",
                transition: "all 0.2s ease-in-out",
              }}
            >
              {index + 1}
            </button>
          ))}
        </div>
      </div>
    </div>

  );
}
