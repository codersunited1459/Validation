
Below are **6 cURLs total** that cover:

* body field validations
* nested DTO validations
* list + element validations
* custom annotation (`@NoWhitespace`)
* cross-field (`@PasswordMatch`)
* path variable validation
* request param validation
* custom annotation on request param

I’m showing **request + representative error response** for each.

> Base URL: `http://localhost:8081`

---

## 1) POST — “Big body failure” (covers most body + nested + list + custom + cross-field)

**Request**

```bash
curl -i -X POST "http://localhost:8081/api/users" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "   ",
    "email": "not-an-email",
    "username": "bad user@",
    "age": 10,
    "dateOfBirth": "2090-01-01",
    "address": { "line1": "", "city": "", "pincode": "50A001" },
    "roles": ["USER", "   "],
    "password": "password123",
    "confirmPassword": "password124"
  }'
```

**Expected error response (400)**

```json
{
  "message": "Validation failed",
  "errors": {
    "name": "name is required",
    "email": "email must be valid",
    "username": "username cannot contain spaces",
    "age": "age must be at least 18",
    "dateOfBirth": "dateOfBirth must be in the past",
    "address.line1": "line1 is required",
    "address.city": "city is required",
    "address.pincode": "pincode must be 6 digits",
    "roles[1]": "role cannot be blank",
    "confirmPassword": "confirmPassword must match password"
  }
}
```

> Notes:
>
> * username may show either whitespace error or pattern error depending on which triggers first; both are “covered” by this request.
> * roles element key might appear as `roles[1]` or `roles`.



====================================================================================

## 2) POST — Missing required fields (covers OnCreate required checks)

**Request**

```bash
curl -i -X POST "http://localhost:8081/api/users" \
  -H "Content-Type: application/json" \
  -d '{}'
```

**Expected error response (400)**

```json
{
  "message": "Validation failed",
  "errors": {
    "name": "name is required",
    "email": "email is required",
    "username": "username is required",
    "age": "age is required",
    "dateOfBirth": "dateOfBirth is required",
    "address": "address is required",
    "roles": "roles cannot be empty on create",
    "password": "password is required",
    "confirmPassword": "confirmPassword is required"
  }
}
```

====================================================================================

## 3) PUT — Update group failure (covers OnUpdate + body validation)

**Request**

```bash
curl -i -X PUT "http://localhost:8081/api/users/101" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "bad-email",
    "username": "bad user"
  }'
```

**Expected error response (400)**

```json
{
  "message": "Validation failed",
  "errors": {
    "id": "id is required for update",
    "email": "email must be valid",
    "username": "username cannot contain spaces"
  }
}
```


====================================================================================

## 4) GET — Path variable validation failure

**Request**

```bash
curl -i "http://localhost:8081/api/users/0"
```

**Expected error response (400)**

```json
{
  "message": "Validation failed",
  "errors": {
    "get.id": "must be greater than or equal to 1"
  }
}
```

> Key may appear as `get.id` / `getById.id` depending on method name.

====================================================================================

## 5) GET — Request param validation failure (covers `q` + `limit`)

**Request**

```bash
curl -i "http://localhost:8081/api/users?q=&limit=0"
```

**Expected error response (400)**

```json
{
  "message": "Validation failed",
  "errors": {
    "search.q": "q is required",
    "search.limit": "must be greater than or equal to 1"
  }
}
```

====================================================================================

## 6) GET — Custom annotation on request param (`@NoWhitespace`)

**Request**

```bash
curl -i "http://localhost:8081/api/users/by-username?username=raghav%2001"
```

**Expected error response (400)**

```json
{
  "message": "Validation failed",
  "errors": {
    "byUsername.username": "username cannot contain spaces"
  }
}
```
====================================================================================

## ✅ 1) POST success — Create user (201)

```bash
curl -i -X POST "http://localhost:8081/api/users" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Raghav",
    "email": "raghav@example.com",
    "username": "raghav_01",
    "age": 25,
    "dateOfBirth": "1999-01-01",
    "address": { "line1": "A-1", "city": "Hyderabad", "pincode": "500001" },
    "roles": ["USER"],
    "password": "password123",
    "confirmPassword": "password123"
  }'
```

**Expected response**

* Status: `201 Created`
* Body (from controller):

  ```
  created id=101
  ```

  (id number may differ)

====================================================================================

## ✅ 2) GET success — Fetch created user (200)

Use the id you got from the POST response (example uses `101`):

```bash
curl -i "http://localhost:8081/api/users/101"
```

**Expected response**

* Status: `200 OK`
* JSON body (example shape):

```json
{
  "id": 101,
  "name": "Raghav",
  "email": "raghav@example.com",
  "username": "raghav_01",
  "age": 25,
  "dateOfBirth": "1999-01-01",
  "address": {
    "line1": "A-1",
    "city": "Hyderabad",
    "pincode": "500001"
  },
  "roles": ["USER"],
  "password": "password123",
  "confirmPassword": "password123"
}
```

> Note: In real APIs you generally **should not return password fields**. We return them here only because we used one DTO for everything to keep the validation demo simple.

====================================================================================


# ✅ 3) PUT Success — Update user (200)

### Request

```bash
curl -i -X PUT "http://localhost:8081/api/users/101" \
  -H "Content-Type: application/json" \
  -d '{
    "id": 101,
    "name": "Raghav Updated",
    "email": "updated@example.com",
    "username": "raghav_updated",
    "age": 26,
    "dateOfBirth": "1998-01-01",
    "address": {
      "line1": "B-10",
      "city": "Hyderabad",
      "pincode": "500002"
    },
    "roles": ["ADMIN"]
  }'
```

### Expected Response

```
HTTP/1.1 200 OK

updated id=101
```

👉 Notes:

* `id` is required in body because of **OnUpdate group**
* Password fields are NOT required in update

====================================================================================

# ✅ 4) GET Search Success — Query params validation passes

### Request

```bash
curl -i "http://localhost:8081/api/users?q=raghav&limit=10"
```

### Expected Response

```json
[
  {
    "id": 101,
    "name": "Raghav Updated",
    "email": "updated@example.com",
    "username": "raghav_updated",
    "age": 26,
    "dateOfBirth": "1998-01-01",
    "address": {
      "line1": "B-10",
      "city": "Hyderabad",
      "pincode": "500002"
    },
    "roles": ["ADMIN"],
    "password": "password123",
    "confirmPassword": "password123"
  }
]
```

====================================================================================

# ✅ 5) GET by username Success — Custom annotation passes

### Request

```bash
curl -i "http://localhost:8081/api/users/by-username?username=raghav_updated"
```

### Expected Response

```
HTTP/1.1 200 OK

lookup username=raghav_updated
```

====================================================================================

# ✅ 6) DELETE Success — Remove user

### Request

```bash
curl -i -X DELETE "http://localhost:8081/api/users/101"
```

### Expected Response

```
HTTP/1.1 204 No Content
```

====================================================================================